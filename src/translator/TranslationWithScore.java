package translator;

import java.util.List;

import com.google.common.collect.Lists;


public class TranslationWithScore {

	private List<String> words;
	private double score;

	public TranslationWithScore(String value, double score) {
		this(Lists.newArrayList(value), score);
	}

	public TranslationWithScore(List<String> words, double score) {
		this.words = words;
		this.score = score;
	}

	public List<String> words() {
		return words;
	}

	public double score() {
		return score;
	}

}
