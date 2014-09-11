package translator;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import latticeGenerator.LatticeGeneratorFileWriter;
import phrase_table.PhraseTableReaderWriter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Multiset;
import common.SerializationUtils;
import common.TextFileUtils;

import edu.stanford.nlp.util.Pair;
import ex1.common.ArpaFormatReadWrite;
import ex1.common.CorpusData;
import ex1.common.Model;
import ex1.eval.CorpusReaderPredicate;
import ex1.eval.EvalArgs;

public class DriverOfTranslate {

	public static void main1(String[] args) {
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
		LatticeGeneratorFileWriter lg = new LatticeGeneratorFileWriter(line, phraseTableMap, "d:\\lattice.txt"); //TODO - parameter
//		Map<Pair<Integer, Integer>, Multiset<Pair<String, Double>>> latticeMap = lg.createLaticeFile();

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

	private String latticeFilename = "phrase_table.txt";
	private PhraseTableReaderWriter ptrw = new PhraseTableReaderWriter(latticeFilename);
	private Map<String, Multiset<Pair<String,Double>>> phraseTableMap;
	private Model lm;
	
	public static void main(String[] args) {
//		new DriverOfTranslate().prepareMap();
//		new DriverOfTranslate().prepareModel();
		new DriverOfTranslate().translate();
	}
	
	private void prepareMap() {
		Stopwatch sw = Stopwatch.createStarted();
		phraseTableMap = ptrw.read();
		SerializationUtils.toFile("binary_map", phraseTableMap);
		System.out.println("prepare took " + sw);
	}
	private void prepareModel() {
		Stopwatch sw = Stopwatch.createStarted();
		lm = new ArpaFormatReadWrite().readFromFile("model.txt");
		SerializationUtils.toFile("binary_model", lm);
		System.out.println("prepare took " + sw);
	}
	private void translate() {
		Stopwatch sw = Stopwatch.createStarted();
		phraseTableMap = SerializationUtils.fromFile("binary_map");
		lm = SerializationUtils.fromFile("binary_model");
		System.out.println("prepare took " + sw);
		Predicate<String> linePredicate = new Predicate<String>(){
			@Override
			public boolean apply(String line) {
				List<String> origin = Splitter.on(" ").splitToList(line);
				doTheWork(origin);
				return true;
			}};
		String file = "test set\\test.heb";
		TextFileUtils.getContentByLines(file, linePredicate);
	}
	private void doTheWork(List<String> origin) {
		Map<Pair<Integer, Integer>, Multiset<Pair<List<String>, Double>>> readLattice = new LatticeGeneratorFileWriter(Joiner.on(" ").join(origin), phraseTableMap, null).createLaticeFile();
		LatticePhraseTranslator phraseTranslator = new LatticePhraseTranslator();
		updateTranslator(phraseTranslator, readLattice, origin);
		StackDecoder stackDecoder = new StackDecoder(origin, phraseTranslator,lm);
		List<String> translate;
		try {
			translate = stackDecoder.translate();
			System.out.println(Joiner.on(" ").join(translate));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error");
		}
//		System.out.println(origin + "=>" + translate);
	}

	private void updateTranslator(LatticePhraseTranslator phraseTranslator,
			Map<Pair<Integer, Integer>, Multiset<Pair<List<String>, Double>>> readLattice, List<String> origin) {
		for (Entry<Pair<Integer, Integer>, Multiset<Pair<List<String>, Double>>> entry : readLattice.entrySet()) {
			Pair<Integer, Integer> key = entry.getKey();
			List<String> originPhrase = Lists.newArrayList(origin.subList(key.first() - 1, key.second()));
			for (Pair<List<String>, Double> pair : entry.getValue()) {
				phraseTranslator.putTranslation(originPhrase, pair.first(), pair.second());
			}
		}
	}
}
