package translator;

import java.util.List;

import com.beust.jcommander.internal.Lists;

public class TranslationWithScore {

	private List<String> words;
	private double score;

	public TranslationWithScore(String value, double score) {
		this.words = Lists.newArrayList(value);
		this.score = score;
	}

	public List<String> words() {
		return words;
	}

	public double score() {
		return score;
	}

}
