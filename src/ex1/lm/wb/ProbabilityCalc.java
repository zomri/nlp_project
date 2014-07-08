package ex1.lm.wb;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Multimap;

import ex1.common.Model;
import ex1.common.WordTuple;
import ex1.common.WordTupleData;

public class ProbabilityCalc {

	private static final Logger log = Logger.getLogger(ProbabilityCalc.class);
	private int n;
	private Map<Integer, Multimap<WordTuple, String>> ngrams;
	private Map<Integer, Map<WordTuple, WordTupleData>> calculated = Maps
			.newHashMap();
	private Map<WordTuple, WordTupleData> allTuples = Maps.newHashMap();
	private int vocaularySize;
	private int corpusSize;

	public ProbabilityCalc(int n,
			Map<Integer, Multimap<WordTuple, String>> ngrams, int vocaularySize) {
		this.n = n;
		this.ngrams = ngrams;
		this.vocaularySize = vocaularySize;
		this.corpusSize = ngrams.get(n).entries().size();
	}

	public ProbabilityCalc(
			Map<Integer, Map<WordTuple, WordTupleData>> ngramsToTuples,
			Model model) {
		this.calculated = ngramsToTuples;
		this.vocaularySize = model.vocabulary().size();
		this.allTuples = calcAllTuples();
	}

	public int calcCorpusSize(Map<WordTuple, WordTupleData> tuples) {
		int count = 0;

		for (Entry<WordTuple, WordTupleData> entry : tuples.entrySet()) {
			count += entry.getValue().count();
		}

		return count;
	}

	private Map<WordTuple, WordTupleData> calcAllTuples() {
		Map<WordTuple, WordTupleData> $ = Maps.newHashMap();
		for (Map<WordTuple, WordTupleData> map : calculated.values()) {
			for (Entry<WordTuple, WordTupleData> e : map.entrySet()) {
				$.put(e.getKey(), e.getValue());
			}
		}
		return $;
	}

	public Map<Integer, Map<WordTuple, WordTupleData>> calculate() {
		for (int i = 1; i <= n; i++) {
			Multimap<WordTuple, String> multimap = ngrams.get(i);
			Map<WordTuple, WordTupleData> value = calcForMultimap(multimap);
			calculated.put(i, value);
		}

		return calculated;
	}

	private Map<WordTuple, WordTupleData> calcForMultimap(
			Multimap<WordTuple, String> multimap) {
		Map<WordTuple, WordTupleData> $ = Maps.newHashMap();
		for (Map.Entry<WordTuple, Collection<String>> e : multimap.asMap()
				.entrySet()) {
			double p = calcProbability(e.getKey(), e.getValue().size());
			Collection<String> wordsAfterTuple = e.getValue();
			log.info("adding tupple with data " + e);
			WordTupleData value = new WordTupleData(p, wordsAfterTuple);
			$.put(e.getKey(), value);
			allTuples.put(e.getKey(), value);
		}
		return $;
	}

	public double calcProbability(WordTuple wordTuple, int count) {
		WordTuple lastWordTuple = wordTuple.lastWordTuple();
		double lambda = 1 / (double) wordTuple.list().size();
		int countLastWord = wordTuple.list().size() == 1 ? count : allTuples
				.get(lastWordTuple) == null ? 1 : allTuples.get(lastWordTuple)
				.count();
		double p = lambda * countLastWord / (vocaularySize + corpusSize);
		for (int j = 1; j < wordTuple.list().size(); j++) {
			WordTuple subTuple = wordTuple.subTuple(j);
			WordTupleData subWordTupleData = allTuples.get(subTuple);

			WordTuple subTupleWithLast = wordTuple.subTupleWithLast(j);
			WordTupleData subWordWithLastTupleData = allTuples
					.get(subTupleWithLast);

			if (null == subWordTupleData) {
				subWordTupleData = new WordTupleData(0, 0, 0);
			}
			int countOfSubTupleWithLast = (null == subWordWithLastTupleData) ? count
					: subWordWithLastTupleData.count();
			double pGiven = 0;
			// for "abc" - we need to CHECK count of "bc" == 0 - not count of
			// "abc" (tuple) or "b" (subTuple). if this is 0 - then USE count of
			// "b" or "ab"...
			// BUT - do we have "bc" data at bigrams stage?

			// not relevant for training
			if (countOfSubTupleWithLast == 0) {
				double Z = (double) (vocaularySize - subWordTupleData
						.countUniqWordsAfter());
				pGiven = subWordTupleData.countUniqWordsAfter() == 0 ? 0
						: subWordTupleData.countUniqWordsAfter()
								/ (Z * (subWordTupleData.countUniqWordsAfter() + subWordTupleData
										.count()));

			} else {
				pGiven = countOfSubTupleWithLast
						/ (subWordTupleData.count() + subWordTupleData
								.countUniqWordsAfter());
			}
			p += lambda * pGiven;
		}
		return p;
	}

	public double calcProbabilityForTest(WordTuple wordTuple, int testCorpusSize) {
		WordTupleData wordTupleData = allTuples.get(wordTuple);
		int count = null == wordTupleData ? 1 : wordTupleData.count();
		return calcProbabilityForTest(wordTuple, count, testCorpusSize);
	}

	public double calcProbabilityForTest(WordTuple wordTuple, int count,
			int testCorpusSize) {
		WordTuple lastWordTuple = wordTuple.lastWordTuple();
		double lambda = 1 / (double) wordTuple.list().size();
		int countLastWord = wordTuple.list().size() == 1 ? count : allTuples
				.get(lastWordTuple) == null ? 1 : allTuples.get(lastWordTuple)
				.count();
		double p = lambda * countLastWord / (vocaularySize + testCorpusSize);
		for (int j = 1; j < wordTuple.list().size(); j++) {
			WordTuple subTuple = wordTuple.subTuple(j);
			WordTupleData subWordTupleData = allTuples.get(subTuple);
			WordTuple subTupleWithLast = wordTuple.subTupleWithLast(j);
			WordTupleData subWordWithLastTupleData = allTuples
					.get(subTupleWithLast);

			if (subWordWithLastTupleData != null) {
				p += lambda * subWordTupleData.p();
				continue;
			}
			subWordWithLastTupleData = new WordTupleData(0, 0, 0);
			double pGiven = 0;
			// unseen

			if (null != subWordTupleData
					&& subWordTupleData.countUniqWordsAfter() != 0) {
				double Z = (double) (vocaularySize - subWordTupleData
						.countUniqWordsAfter());
				pGiven = subWordTupleData.countUniqWordsAfter()
						/ (Z * (subWordTupleData.countUniqWordsAfter() + subWordTupleData
								.count()));
			}
			p += lambda * pGiven;
		}
		return Math.log(p);
	}
}
