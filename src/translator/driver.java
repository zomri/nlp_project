package translator;

import java.util.Map;

import latticeGenerator.LatticeGenerator;
import phrase_table.PhraseTableReaderWriter;

import com.beust.jcommander.JCommander;
import com.google.common.collect.Multiset;

import edu.stanford.nlp.util.Pair;
import ex1.common.ArpaFormatReadWrite;
import ex1.common.CorpusData;
import ex1.common.Model;
import ex1.eval.CorpusReaderPredicate;
import ex1.eval.EvalArgs;

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
		TranslatorArgs cliArgs = new TranslatorArgs();
		JCommander jCommander = new JCommander(cliArgs, args);
		
		//Will this work?
		EvalArgs evalArgs = new EvalArgs();
		JCommander jCommander2 = new JCommander(evalArgs, args);
		
		//Reading model from file
		Model model = new ArpaFormatReadWrite().readFromFile(evalArgs);//need only model file
		CorpusReaderPredicate linePredicate = new CorpusReaderPredicate(
				model.n());

		//Reading phrase table from file
		//TextFileUtils.getContent(
		PhraseTableReaderWriter ptrw = new PhraseTableReaderWriter(cliArgs.phraseTableFile());
		Map<String, Multiset<Pair<String,Double>>> phraseTableMap = ptrw.read();

		//Reading input sentences to be translate
		//Foreach sentence - create lattice file
		//Translate sentence using the StackDecoder (should be initialized with Model and work with the lattice file data)
		
		String line = "";
		LatticeGenerator lg = new LatticeGenerator(line, phraseTableMap, "d:\\lattice.txt"); //TODO - parameter
		Map<Pair<Integer, Integer>, Multiset<Pair<String, Double>>> latticeMap = lg.createLaticeFile();

		//TODO - question - what the hell should be the testCorpusSize??
		
		//Basic code (not finished) to calc the LM score
		//TODO - all of this should be in Ohad's code somewhere!
		
//		linePredicate.apply(/*list of words from hypothesis*/);
		//		new CorpusReader(cliArgs.inputfile(), linePredicate).read();
		CorpusData corpusData = linePredicate.getCorpusData();
		model.setTestCorpusSize(corpusData.tuples().size());
//		
//		//TODO - not sure how to use the ex1 code to calc the hypothesis (how to break to ngrams)
//		double prob = new LmProbCalculator(model, corpusData).calculate();
		
		 

	}

}
