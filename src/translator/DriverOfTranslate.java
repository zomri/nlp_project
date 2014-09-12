package translator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import latticeGenerator.LaticeGeneratorFromFileReader;
import latticeGenerator.LatticeGeneratorFileWriter;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;

import phrase_table.PhraseTableReaderWriter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Multiset;
import common.ExceptionUtils;
import common.SerializationUtils;
import common.TextFileUtils;

import edu.stanford.nlp.util.Pair;
import ex1.common.ArpaFormatReadWrite;
import ex1.common.Model;

public class DriverOfTranslate {

//	private String latticeFilename = "phrase_table.txt";
//	private String fileToTranslate = "test set\\test.heb";
	private Map<String, Multiset<Pair<String,Double>>> phraseTableMap;
	private Model lm;
	private TranslatorArgs cliArgs = new TranslatorArgs();
	private OutputStreamWriter writer;
	
	public static void main(String[] args) throws IOException {
//		args = new String[]{"-o","output_file","-i","test set\\test.heb"};
		BasicConfigurator.configure(new FileAppender(new SimpleLayout(), "log_file"));
		new DriverOfTranslate().translate(args);
	}
	
	private void prepareMap() {
		Stopwatch sw = Stopwatch.createStarted();
		if (null != cliArgs.latticeFile()) {
			return;
		}
		if (null != cliArgs.phraseTableFile()) {
			phraseTableMap = new PhraseTableReaderWriter(cliArgs.phraseTableFile()).read();
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
		System.out.println("parameters are " + cliArgs);
		prepareMap();
		prepareModel();
	}
	private void doTheWork(List<String> origin) {
		Map<Pair<Integer, Integer>, Multiset<Pair<List<String>, Double>>> readLattice = readLatticeFile(origin);
		LatticePhraseTranslator phraseTranslator = new LatticePhraseTranslator();
		updateTranslator(phraseTranslator, readLattice, origin);
		StackDecoder stackDecoder = new StackDecoder(origin, phraseTranslator,lm, cliArgs);
		Pair<Double, List<String>> translate;
		try {
			translate = stackDecoder.translateWithScore();
			writeToFile(translate.first() + " " + Joiner.on(" ").join(translate.second()));
		} catch (Exception e) {
			e.printStackTrace();
			writeToFile("error");
		}
	}

	private Map<Pair<Integer, Integer>, Multiset<Pair<List<String>, Double>>> readLatticeFile(List<String> origin) {
		if (null != cliArgs.latticeFile()) {
			return new LaticeGeneratorFromFileReader(cliArgs.latticeFile()).readLattice();
		}
		Map<Pair<Integer, Integer>, Multiset<Pair<List<String>, Double>>> readLattice = new LatticeGeneratorFileWriter(Joiner.on(" ").join(origin), phraseTableMap, null).createLaticeFile();
		return readLattice;
	}
	
	private void writeToFile(String line) {
		if (cliArgs.outputFile() == null) {
			System.out.println(line);
		}
		else {
			if (null == writer) {
				writer = TextFileUtils.getWriter(cliArgs.outputFile());
			}
			try {
				writer.write(line + "\n");
				writer.flush();
			} catch (IOException e) {
				throw ExceptionUtils.asUnchecked(e);
			}
		}
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
