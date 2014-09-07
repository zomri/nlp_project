package translator;

import com.beust.jcommander.JCommander;

import ex1.common.ArpaFormatReadWrite;
import ex1.common.CorpusData;
import ex1.common.CorpusReader;
import ex1.common.Model;
import ex1.eval.CorpusReaderPredicate;
import ex1.eval.EvalArgs;
import ex1.eval.PreplexityCalculator;

public class driver {

	public static void main(String[] args) {
		/* TODO:
		 * - read language model file (create one first, tokenization before!)
		 * - read sentences in hebrew 
		 * - foreach sentence:
		 * 		- create lattice file
		 * 		- run translator with lattice file and LM file as input
		 *		  
		 * - see how translation behaves (by looking)
		 * - perhaps run Bleau-score script? 
		 *  
		 */
		
		//TODO - how to calculate LM score? - 
		//according to forum - calculate by sum of log-probs of sub-ngrams (something like perplexity)
		//for unseen ngrams - use unseen event.
		// Need to add <s> </s> at begin/end of sentence?
		
		//-i inputfile -m model file
		
		EvalArgs cliArgs = new EvalArgs();
		JCommander jCommander = new JCommander(cliArgs, args);
		
		Model model = new ArpaFormatReadWrite().readFromFile(cliArgs);
//		CorpusReaderPredicate linePredicate = new CorpusReaderPredicate(
//				model.n());
//		new CorpusReader(cliArgs.inputfile(), linePredicate).read();
//		CorpusData corpusData = linePredicate.getCorpusData();
//		model.setTestCorpusSize(corpusData.tuples().size());
//		
//		//TODO - not sure how to use the ex1 code to calc the hypothesis (how to break to ngrams)
//		double preplexity = new LmProbCalculator(model, corpusData)
//				.calculate();
		
		 

	}

}
