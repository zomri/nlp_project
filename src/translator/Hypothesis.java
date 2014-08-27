package translator;

import java.util.List;

import com.beust.jcommander.internal.Lists;

public class Hypothesis implements Comparable<Hypothesis>{

	private List<Hypothesis> prev = Lists.newArrayList();
	private List<Hypothesis> next = Lists.newArrayList();
	private List<String> words = Lists.newArrayList();
	private List<Boolean> coverage = Lists.newArrayList();
	private double score;
	
	public Hypothesis(List<String> words, List<Boolean> coverage) {
		this.coverage = coverage;
		this.words = words;
	}

//	public Hypothesis() {
//		super();
//	}

	public Hypothesis(int sentenceSize) {
		for (int i = 0; i < sentenceSize; i++) {
			coverage.add(false);
		}
	}

	public List<Hypothesis> prev() {
		return prev;
	}
	public List<Hypothesis> next() {
		return next;
	}

	public List<String> words() {
		return words;
	}

	@Override
	public int compareTo(Hypothesis o) {
		return 0;
	}

	@Override
	public String toString() {
		return "Hypothesis [words=" + words + ", coverage=" + coverage
				+ ", score=" + score + "]";
	}

	public List<Boolean> coverage() {
		return coverage;
	}
	
	
}
