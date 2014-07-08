package ex1.lm.ls;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.beust.jcommander.internal.Maps;
import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import ex1.common.Model;
import ex1.common.WordTuple;
import ex1.common.WordTupleData;

public class LsModel implements Model {

	private static Logger log = Logger.getLogger(LsModel.class);

	private int n;
	private double lambda;
	private Map<Integer, Map<WordTuple, WordTupleData>> ngramsToTuples;
	private Map<WordTuple, Double> probMap;
	private Set<String> vocabulary = Sets.newHashSet();

	public LsModel(int n, double lambda,
			Map<Integer, Map<WordTuple, WordTupleData>> ngramsToTuples) {
		this.n = n;
		this.lambda = lambda;
		this.ngramsToTuples = ngramsToTuples;
		this.vocabulary = calcVocabulary();
	}

	private Set<String> calcVocabulary() {
		Set<String> $ = Sets.newHashSet();
		Map<WordTuple, WordTupleData> map = ngramsToTuples.get(n);
		for (WordTuple w : map.keySet()) {
			$.addAll(w.list());
		}
		return $;
	}

	public LsModel(int n, double lambda, String smoothing,
			Map<Integer, Multiset<WordTuple>> map, Set<String> vocabulary) {
		this.n = n;
		this.lambda = lambda;
		this.vocabulary = vocabulary;
		this.ngramsToTuples = calculate(map);
	}

	private Map<Integer, Map<WordTuple, WordTupleData>> calculate(
			Map<Integer, Multiset<WordTuple>> ngramsToTuples2) {
		Map<Integer, Map<WordTuple, WordTupleData>> $ = Maps.newHashMap();
		$.put(n, getNgramsLogProb(n, ngramsToTuples2));
		$.put(n - 1, getNgramsMinusOneCount(ngramsToTuples2.get(n - 1)));
		return $;
	}

	private Map<WordTuple, WordTupleData> getNgramsMinusOneCount(
			Multiset<WordTuple> multiset) {
		Map<WordTuple, WordTupleData> $ = Maps.newHashMap();
		for (Entry<WordTuple> e : multiset.entrySet()) {
			$.put(e.getElement(), new WordTupleData(0, e.getCount()));
		}
		return $;
	}

	private HashMap<WordTuple, WordTupleData> getNgramsLogProb(int ngram,
			Map<Integer, Multiset<WordTuple>> ngramsToTuples2) {
		HashMap<WordTuple, WordTupleData> $ = new HashMap<WordTuple, WordTupleData>();
		Multiset<WordTuple> nGrams = ngramsToTuples2.get(new Integer(ngram));
		Multiset<WordTuple> nMinus1Grams = ngramsToTuples2.get(new Integer(
				ngram - 1));
		// iterate entries of ngrams - and calculate p with n-1grams and other
		// paramters.
		for (Multiset.Entry<WordTuple> entry : nGrams.entrySet()) {

			log.info(entry.getElement() + " -> " + entry.getCount());
			if (entry.getCount() > 1) {
				log.info("more than one time");
			}
			int ngramCount = entry.getCount();
			int nMinus1Count = nMinus1Grams.count(new WordTuple(entry
					.getElement().list()
					.subList(0, entry.getElement().list().size() - 1)));
			double p = Math.log((ngramCount + lambda)
					/ (nMinus1Count + lambda * vocabulary.size()));

			$.put(entry.getElement(), new WordTupleData(p, entry.getCount()));
		}

		return $;
	}

	public double lambda() {
		return lambda;
	}

	public int n() {
		return n;
	}

	public Map<Integer, Map<WordTuple, WordTupleData>> ngramsByCount() {
		return ngramsToTuples;
	}

	public Map<WordTuple, Double> getProbMap() {
		return probMap;
	}

	public void setProbMap(Map<WordTuple, Double> probMap) {
		this.probMap = probMap;
	}

	public Set<String> vocabulary() {
		return vocabulary;
	}

	public double getProb(WordTuple wordTuple) {
		WordTupleData w = ngramsToTuples.get(n).get(wordTuple);
		if (null == w) {
			log.debug("data not found for " + wordTuple);
			WordTuple wtMinusOne = new WordTuple(wordTuple.list().subList(0,
					wordTuple.list().size() - 1));
			WordTupleData wordTupleData = ngramsToTuples.get(n - 1).get(
					wtMinusOne);
			int countMinusOne = 0;
			if (null != wordTupleData) {
				log.debug("data found for w-1: " + wordTupleData.count());
				countMinusOne = wordTupleData.count();
			}
			double calculatedP = Math.log(lambda
					/ (double) (countMinusOne + lambda * vocabulary.size()));
			log.debug("calculatedP " + calculatedP);
			return calculatedP;
		}
		log.debug("data found for " + wordTuple);
		return w.p();

	}

	@Override
	public void setTestCorpusSize(int size) {
	}

}
