package ex1.eval;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;

import ex1.common.ArpaFormatReadWrite;
import ex1.common.CorpusData;
import ex1.common.CorpusReader;
import ex1.common.Model;

public class Eval {
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
		EvalArgs cliArgs = new EvalArgs();
		JCommander jCommander = new JCommander(cliArgs, args);
		if (cliArgs.inputfile() == null || cliArgs.modelfile() == null) {
			System.out.println("missing arguments");
			jCommander.setProgramName("eval");
			jCommander.usage();
			System.exit(1);
		}
		Model model = new ArpaFormatReadWrite().readFromFile(cliArgs);
		CorpusReaderPredicate linePredicate = new CorpusReaderPredicate(
				model.n());
		new CorpusReader(cliArgs.inputfile(), linePredicate).read();
		CorpusData corpusData = linePredicate.getCorpusData();
		model.setTestCorpusSize(corpusData.tuples().size());
		double preplexity = new PreplexityCalculator(model, corpusData)
				.calculate();
		System.out.println(/* "preplexity = " + */preplexity);
	}
}
