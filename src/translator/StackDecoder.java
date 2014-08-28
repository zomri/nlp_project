package translator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.beust.jcommander.internal.Lists;

public class StackDecoder {

	private static final int MAX_STACK_SIZE = 10;
	private static final int MAX_TRANSLATE_SEQUENCE = 5;
	private List<String> origin;
	private List<SortedSet<Hypothesis>> stacks = Lists.newArrayList();
	private LatticePhraseTranslator phraseTranslator;
	
	public StackDecoder(List<String> origin, LatticePhraseTranslator phraseTranslator) {
		super();
		this.origin = origin;
		this.phraseTranslator = phraseTranslator;
	}

	public List<String> translate(){
		if (null == origin || origin.isEmpty()) {
			return Lists.newArrayList();
		}
		init();
		for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++) {
			for (Hypothesis hypothesis : stacks.get(stackIndex)) {
				List<Hypothesis> trasnlations = getAllHypothesis(hypothesis);
				for (Hypothesis translationOption : trasnlations) {
					translationOption.prev().add(hypothesis);
					hypothesis.next().add(translationOption);
					updateScore(translationOption);
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
		if (stack.size() > MAX_STACK_SIZE) {
			stack.remove(stack.first());
		}
	}

	private void updateScore(Hypothesis translationOption) {
		// TODO Auto-generated method stub
		//at this point score only contains lattice score
		double latticeScore = translationOption.score();
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

	private List<String> getTranslationFromStacks() {
		List<String> translation = Lists.newArrayList();
		Hypothesis last = stacks.get(stacks.size()-1).last();
		boolean finished = false;
		while (!finished){
			translation.addAll(0, last.translation());
			List<Hypothesis> prev = last.prev();
			if (prev.isEmpty()) {
				finished = true;
			}
			else {
				last = prev.get(0);
			}
		}
		return translation;
	}
}
