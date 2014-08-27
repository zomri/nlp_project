package translator;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.beust.jcommander.internal.Lists;

public class StackDecoder {

	private List<String> origin;
	private List<SortedSet<Hypothesis>> stacks = Lists.newArrayList();
	private PhraseTranslator phraseTranslator;
	
	public StackDecoder(List<String> origin, PhraseTranslator phraseTranslator) {
		super();
		this.origin = origin;
		this.phraseTranslator = phraseTranslator;
		for (int i = 0; i < origin.size()+1; i++) {
			stacks.add(new TreeSet<>());
		}
	}

	public List<String> translate(){
		if (null == origin || origin.isEmpty()) {
			return Lists.newArrayList();
		}
		stacks.get(0).add(new Hypothesis(origin.size()));
		for (int stackIndex = 0; stackIndex < stacks.size(); stackIndex++) {
			for (Hypothesis hypothesis : stacks.get(stackIndex)) {
				List<Hypothesis> trasnlations = getAllHypothesis(hypothesis);
				for (Hypothesis translationOption : trasnlations) {
					translationOption.prev().add(hypothesis);
					hypothesis.next().add(translationOption);
					int newStackIndex = stackIndex + translationOption.words().size();
					stacks.get(newStackIndex).add(translationOption);
//					recombine with existing hypothesis if possible
//					prune stack if too big
				}
			}
		}
		return getTranslationFromStacks();
	}



	private List<Hypothesis> getAllHypothesis(Hypothesis hypothesis) {
		List<Hypothesis> $ = Lists.newArrayList();
		List<Boolean> coverage = hypothesis.coverage();
		if (coverage.stream().allMatch(x -> x)) {
			return Lists.newArrayList();
		}
		List<String> words = pickNextOrigin(coverage);
		$.add(new Hypothesis(phraseTranslator.getTranslation(words), calcNewCoverage(hypothesis.coverage(), words)));
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

	private List<String> pickNextOrigin(List<Boolean> coverage) {
		for (int i = 0; i < coverage.size(); i++) {
			if (!coverage.get(i)) {
				return Lists.newArrayList(origin.get(i));
			}
		}
		return Lists.newArrayList();
	}

	private List<String> getTranslationFromStacks() {
		List<String> translation = Lists.newArrayList();
		Hypothesis last = stacks.get(stacks.size()-1).first();
		boolean finished = false;
		while (!finished){
			translation.addAll(0, last.words());
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
