package ex1.eval;

import java.util.Set;

import ex1.common.CorpusData;
import ex1.common.Model;
import ex1.common.WordTuple;

public class PreplexityCalculator {

	private Model model;
	private CorpusData corpusData;

	public PreplexityCalculator(Model model, CorpusData corpusData) {
		this.model = model;
		this.corpusData = corpusData;
	}

	public double calculate() {
		double p = 0;
		Set<String> modelVocaulary = model.vocabulary();
		for (WordTuple wordTuple : corpusData.tuples()) {
			WordTuple wordTupleWithUnk = WordTuple.createByVocabulary(
					wordTuple, modelVocaulary);
			double prob = model.getProb(wordTupleWithUnk);
			p += prob;
		}
		return formula(p);
	}

	private double formula(double p) {
		double a = -1 / corpusTuplesSize();
		return a * p;
	}

	private double corpusTuplesSize() {
		return corpusData.tuplesSize();
	}

}
