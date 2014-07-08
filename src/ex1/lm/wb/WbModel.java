package ex1.lm.wb;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Multimap;

import ex1.common.Model;
import ex1.common.WordTuple;
import ex1.common.WordTupleData;

public class WbModel implements Model {

	private static Logger log = Logger.getLogger(WbModel.class);

	private int n;
	private double lambda;
	private Map<Integer, Map<WordTuple, WordTupleData>> ngramsToTuples;
	private Map<WordTuple, Double> probMap;
	private Set<String> vocabulary = Sets.newHashSet();
	private ProbabilityCalc probabilityCalc;
	private int testCorpusSize;

	public WbModel(int n, double lambda,
			Map<Integer, Map<WordTuple, WordTupleData>> ngramsToTuples) {
		this.n = n;
		this.lambda = lambda;
		this.ngramsToTuples = ngramsToTuples;
		this.vocabulary = calcVocabulary();
		this.probabilityCalc = new ProbabilityCalc(ngramsToTuples, this);
	}

	public WbModel(int n, double lambda, String smoothing,
			Map<Integer, Multimap<WordTuple, String>> ngrams,
			Set<String> vocabulary) {
		this.n = n;
		this.lambda = lambda;
		this.vocabulary = vocabulary;
		this.ngramsToTuples = new ProbabilityCalc(n, ngrams, vocabulary.size())
				.calculate();
	}

	private Set<String> calcVocabulary() {
		Set<String> $ = Sets.newHashSet();
		Map<WordTuple, WordTupleData> map = ngramsToTuples.get(n);
		for (WordTuple w : map.keySet()) {
			$.addAll(w.list());
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
		if (null != w) {
			log.debug("data found for " + wordTuple);
			return w.p();
		}
		log.debug("data NOT found for " + wordTuple);
		return probabilityCalc
				.calcProbabilityForTest(wordTuple, testCorpusSize);
	}

	public void setTestCorpusSize(int testCorpusSize) {
		this.testCorpusSize = testCorpusSize;
	}

}
