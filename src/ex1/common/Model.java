package ex1.common;

import java.util.Map;
import java.util.Set;

public interface Model {

	Map<Integer, Map<WordTuple, WordTupleData>> ngramsByCount();

	Set<String> vocabulary();

	double getProb(WordTuple wordTupleWithUnk);

	int n();

	void setTestCorpusSize(int size);

}
