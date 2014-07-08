package ex1.eval;

import java.util.List;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Predicate;

import ex1.common.CorpusData;
import ex1.common.WordTuple;

public class CorpusReaderPredicate implements Predicate<List<String>> {

	private List<WordTuple> tuples = Lists.newArrayList();
	private int ngrams;

	public CorpusReaderPredicate(int ngrams) {
		super();
		this.ngrams = ngrams;
	}

	@Override
	public boolean apply(List<String> list) {
		for (int i = 0; i < list.size() - ngrams + 1; i++) {
			List<String> subList = list.subList(i, i + ngrams);
			tuples.add(new WordTuple(subList));
		}
		return true;
	}

	public CorpusData getCorpusData() {
		return new CorpusData(tuples);
	}

}
