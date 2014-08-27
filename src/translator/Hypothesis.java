package translator;

import java.util.List;

import com.beust.jcommander.internal.Lists;

public class Hypothesis implements Comparable<Hypothesis>{

	private List<Hypothesis> prev = Lists.newArrayList();
	private List<Hypothesis> next = Lists.newArrayList();
	private List<String> translation = Lists.newArrayList();
	private List<Boolean> coverage = Lists.newArrayList();
	private List<String> words = Lists.newArrayList();
	private double score;
	
	public Hypothesis(List<String> words, TranslationWithScore translationWithScore, List<Boolean> coverage) {
		this.words = words;
		this.coverage = coverage;
		this.translation = translationWithScore.words();
		this.score = translationWithScore.score();
	}

//	public Hypothesis() {
//		super();
//	}

	public Hypothesis(int sentenceSize) {
		for (int i = 0; i < sentenceSize; i++) {
			coverage.add(false);
		}
	}
	
	public double totalScore() {
		return score + prev.stream().mapToDouble(x->x.totalScore()).max().orElse(0);
	}

	public List<Hypothesis> prev() {
		return prev;
	}
	public List<Hypothesis> next() {
		return next;
	}

	public List<String> translation() {
		return translation;
	}

	@Override
	public int compareTo(Hypothesis o) {
		return Double.compare(totalScore(), o.totalScore());
	}

	@Override
	public String toString() {
		return "Hypothesis ["
				+ (words != null ? "words=" + words + ", " : "")
				+ (translation != null ? "translation=" + translation + ", "
						: "")
				+ (coverage != null ? "coverage=" + coverage + ", " : "")
				+ "score=" + score + ", totalScore()=" + totalScore() + "]";
	}

	public List<Boolean> coverage() {
		return coverage;
	}

	public double score() {
		return score;
	}

	public List<String> words() {
		return words;
	}
	
	
}
