package translator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Joiner;

import edu.stanford.nlp.util.Pair;
import ex1.common.CorpusData;
import ex1.common.Model;
import ex1.eval.CorpusReaderPredicate;

public class StackDecoder {

	public static final String NA = "*NA*";
	private int MAX_STACK_SIZE = 10;
	private static final int MAX_TRANSLATE_SEQUENCE = 5;
	private static final double NA_SCORE = 0;//-1000;
	private List<String> origin;
	private List<SortedSet<Hypothesis>> stacks = Lists.newArrayList();
	private LatticePhraseTranslator phraseTranslator;
	private Model model;
	private double lambdaTrans;
	private double lambdaLm;
	
	public StackDecoder(List<String> origin, LatticePhraseTranslator phraseTranslator, Model model, TranslatorArgs cliArgs) {
		super();
		this.origin = origin;
		this.phraseTranslator = phraseTranslator;
		this.model = model;
		MAX_STACK_SIZE = cliArgs.histogramPruningLimit();
		if (MAX_STACK_SIZE <= 0) {
			throw new IllegalArgumentException("histogramPruningLimit must be positive integer " + MAX_STACK_SIZE);
		}
		lambdaTrans = cliArgs.lambdaTranslation();
		lambdaLm = cliArgs.lambdaLanguageModel();
	}
	public List<String> translate(){
		return translateWithScore().second();
	}
	public Pair<Double, List<String>> translateWithScore() {
		if (null == origin || origin.isEmpty()) {
			return new Pair<Double, List<String>>(0.0, Lists.<String>newArrayList());
		}
		init();
		for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++) {
			for (Hypothesis hypothesis : stacks.get(stackIndex)) {
				List<Hypothesis> trasnlations = getAllHypothesis(hypothesis);
				for (Hypothesis translationOption : trasnlations) {
					translationOption.prev().add(hypothesis);
					hypothesis.next().add(translationOption);
					int newStackIndex = stackIndex + translationOption.words().size();
					stacks.get(newStackIndex).add(translationOption);
					recombine(newStackIndex);
					pruneStack(newStackIndex);
				}
			}
		}
		return getTranslationFromStacks();
	}

	private void init() {
		for (int i = 0; i < origin.size()+1; i++) {
			stacks.add(new TreeSet<>());
		}
		stacks.get(0).add(new Hypothesis(origin.size()));
	}

	private void recombine(int newStackIndex) {
		SortedSet<Hypothesis> stack = stacks.get(newStackIndex);
		List<Hypothesis> allHypothesis = Lists.newArrayList(stack);
		for (Hypothesis hypothesis : allHypothesis) {
			Iterator<Hypothesis> iterator = stack.headSet(hypothesis).iterator();
			while (iterator.hasNext()) {
				Hypothesis h = iterator.next();
				if (h.coverage().equals(hypothesis.coverage())
						&& h.translation().equals(hypothesis.translation())) {
					iterator.remove();
					List<Hypothesis> pointers = h.prev();
					for (Hypothesis prevH : pointers) {
						prevH.next().remove(h);
						prevH.next().add(hypothesis);
					}
					hypothesis.prev().addAll(pointers);
				}
			}
		}
		
	}

	private void pruneStack(int newStackIndex) {
		SortedSet<Hypothesis> stack = stacks.get(newStackIndex);
		while (stack.size() > MAX_STACK_SIZE) {
			stack.remove(stack.first());
		}
	}

	private List<Hypothesis> getAllHypothesis(Hypothesis hypothesis) {
		List<Hypothesis> $ = Lists.newArrayList();
		List<Boolean> coverage = hypothesis.coverage();
		if (coverage.stream().allMatch(x -> x)) {
			return Lists.newArrayList();
		}
		List<List<String>> wordsList = pickNextOrigins(coverage);
		for (List<String> words : wordsList) {
			List<Boolean> newCoverage = calcNewCoverage(hypothesis.coverage(), words);
			Collection<TranslationWithScore> translations = phraseTranslator.getTranslation(words);
			if (translations.isEmpty() && words.size() == 1) {
				translations.add(new TranslationWithScore(NA, NA_SCORE));
			}
			for (TranslationWithScore translationWithScore : translations) {
				$.add(new Hypothesis(words, translationWithScore, newCoverage));
			}
		}
		return $;
	}

	private List<Boolean> calcNewCoverage(List<Boolean> coverage, List<String> words) {
		List<Boolean> $ = Lists.newArrayList(coverage);
		for (String word : words) {
			int index = findUncoveredIndex($, word);
			$.remove(index);
			$.add(index, true);
		}
		return $;
	}

	private int findUncoveredIndex(List<Boolean> coverage, String word) {
		for (int i = 0; i < coverage.size(); i++) {
			if (!coverage.get(i) && origin.get(i).equals(word)) {
				return i;
			}
		}
		return -1;
	}

	private List<List<String>> pickNextOrigins(List<Boolean> coverage) {
		List<List<String>> $ = Lists.newArrayList();
		for (int i = 0; i < coverage.size(); i++) {
			for (int j = i + 1; j <= coverage.size() && j - i <= MAX_TRANSLATE_SEQUENCE; j++) {
				if (coverage.get(j-1)) {
					//already covered word
					break;
				}
				$.add(Lists.newArrayList(origin.subList(i, j)));
			}
			if (!coverage.get(i)) {
				//found not covered word, finish
				break;
			}
		}
		return $;
	}

	private Pair<Double, List<String>> getTranslationFromStacks() {
		Pair<Double, List<String>> bestTranslation = null;
		Pair<Double, List<String>> translation = null;
		Double maxScore = Double.NEGATIVE_INFINITY;
		
		if (0 == Double.compare(0, lambdaLm)) {
			return getTranslationFromStacks(stacks.get(stacks.size()-1).last());
		}
		for (Hypothesis h : stacks.get(stacks.size()-1))
		{
			translation = getTranslationFromStacks(h);
			
			CorpusReaderPredicate linePredicate = new CorpusReaderPredicate(
					model.n());
			new LineReader(Joiner.on(" ").join(translation.second()), linePredicate).read();
			CorpusData corpusData = linePredicate.getCorpusData();
			model.setTestCorpusSize(corpusData.tuples().size());
			
			Double score = lambdaTrans * h.totalScore() + lambdaLm * (new LmProbCalculator(model, corpusData)).calculate();
			if (score > maxScore)
			{
				maxScore = score;
				bestTranslation = translation;
			}
		}
		return bestTranslation;
	}
	
	private Pair<Double, List<String>> getTranslationFromStacks(Hypothesis hypo) {
		List<String> translation = Lists.newArrayList();
		double score = hypo.totalScore();
		
		boolean finished = false;
		while (!finished){
			translation.addAll(0, hypo.translation());
			List<Hypothesis> prev = hypo.prev();
			if (prev.isEmpty()) {
				finished = true;
			}
			else {
				hypo = prev.get(0);
			}
		}
		return new Pair<Double, List<String>>(score, translation);
	}

}
