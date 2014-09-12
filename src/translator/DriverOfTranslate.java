package translator;

import java.io.File;
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
import ex1.common.Model;

public class DriverOfTranslate {

//	private String latticeFilename = "phrase_table.txt";
//	private String fileToTranslate = "test set\\test.heb";
	private PhraseTableReaderWriter ptrw;
	private Map<String, Multiset<Pair<String,Double>>> phraseTableMap;
	private Model lm;
	private TranslatorArgs cliArgs = new TranslatorArgs();
	
	public static void main(String[] args) {
		new DriverOfTranslate().translate(args);
	}
	
	private void prepareMap() {
		Stopwatch sw = Stopwatch.createStarted();
		if (null != cliArgs.phraseTableFile()) {
			ptrw = new PhraseTableReaderWriter(cliArgs.phraseTableFile());
			phraseTableMap = ptrw.read();
	//		SerializationUtils.toFile("binary_map", phraseTableMap);
		} else if (new File("binary_map").exists()) {
			phraseTableMap = SerializationUtils.fromFile("binary_map");
		}
		System.out.println("prepareMap took " + sw);
	}
	private void prepareModel() {
		Stopwatch sw = Stopwatch.createStarted();
		if (null != cliArgs.modelFile()) {
			lm = new ArpaFormatReadWrite().readFromFile(cliArgs.modelFile());
//			SerializationUtils.toFile("binary_model", lm);
		} else if (new File("binary_model").exists()) {
			lm = SerializationUtils.fromFile("binary_model");
		}
		System.out.println("prepareModel took " + sw);
	}
	private void translate(String[] args) {
		init(args);
		Predicate<String> linePredicate = new Predicate<String>(){
			@Override
			public boolean apply(String line) {
				List<String> origin = Splitter.on(" ").splitToList(line);
				doTheWork(origin);
				return true;
			}};
		TextFileUtils.getContentByLines(cliArgs.fileToTranslate(), linePredicate);
	}

	private void init(String[] args) {
		new JCommander(cliArgs, args);
		prepareMap();
		prepareModel();
	}
	private void doTheWork(List<String> origin) {
		Map<Pair<Integer, Integer>, Multiset<Pair<List<String>, Double>>> readLattice = new LatticeGeneratorFileWriter(Joiner.on(" ").join(origin), phraseTableMap, null).createLaticeFile();
		LatticePhraseTranslator phraseTranslator = new LatticePhraseTranslator();
		updateTranslator(phraseTranslator, readLattice, origin);
		StackDecoder stackDecoder = new StackDecoder(origin, phraseTranslator,lm, cliArgs);
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
