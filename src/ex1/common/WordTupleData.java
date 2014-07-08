package ex1.common;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;

public class WordTupleData {

	private static final Logger log = Logger.getLogger(WordTupleData.class);
	private double p;
	private int count;
	private int uniqWordsAfterTuple;

	public WordTupleData(double p, int count) {
		this.p = p;
		this.count = count;
	}

	public WordTupleData(double p2, Collection<String> wordsAfterTuple) {
		this(p2, wordsAfterTuple.size());
		this.uniqWordsAfterTuple = new HashSet<String>(wordsAfterTuple).size();
		if (countUniqWordsAfter() != count()) {
			log.debug("countUniqWordsAfter " + countUniqWordsAfter()
					+ " countNonUniqWordsAfter() " + count());
		}
	}

	public WordTupleData(double p, int count, int uniqWordsAfterTuple) {
		super();
		this.p = p;
		this.count = count;
		this.uniqWordsAfterTuple = uniqWordsAfterTuple;
	}

	public double p() {
		return p;
	}

	public int count() {
		return count;
	}

	@Override
	public String toString() {
		return "WordTupleData [p=" + p + ", count=" + count + "]";
	}

	public int countUniqWordsAfter() {
		return uniqWordsAfterTuple;
	}

}
