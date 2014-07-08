package ex1.common;

import java.util.List;
import java.util.Set;

import com.beust.jcommander.internal.Sets;

public class CorpusData {

	private List<WordTuple> tuples;

	public CorpusData(List<WordTuple> tuples) {
		super();
		this.tuples = tuples;
	}

	public List<WordTuple> tuples() {
		return tuples;
	}

	public int vocabularySize() {
		Set<String> vocabulary = Sets.newHashSet();
		for (WordTuple w : tuples) {
			vocabulary.addAll(w.list());
		}
		return vocabulary.size();
	}

	public double tuplesSize() {
		return tuples.size();
	}

}
