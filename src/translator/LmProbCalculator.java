package translator;

import ex1.common.CorpusData;
import ex1.common.Model;
import ex1.eval.PreplexityCalculator;

public class LmProbCalculator extends PreplexityCalculator {

	public LmProbCalculator(Model model, CorpusData corpusData) {
		super(model, corpusData);
		// TODO Auto-generated constructor stub
	}

	protected double formula(double p) {
		return p;
	}
}
