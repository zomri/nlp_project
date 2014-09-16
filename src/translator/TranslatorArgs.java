package translator;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class TranslatorArgs {

	@Parameter
	public List<String> parameters = Lists.newArrayList();
	@Parameter(names = { "-p", }, description = "phrase table file")
	private String phraseTableFile;
	@Parameter(names = { "--sentence", }, description = "sentence to translate")
	private String sentence;
	@Parameter(names = { "-i", }, description = "input file to translate")
	private String fileToTranslate;
	@Parameter(names = { "-o", }, description = "output file (if empty will print to stdout)")
	private String outputFile;
	@Parameter(names = { "-m", }, description = "model file")
	private String modelFile;
	@Parameter(names = { "--latticeFile", }, description = "lattice file")
	private String latticeFile;
	@Parameter(names = { "--lambdaTranslation", }, description = "lambda of translation")
	private double lambdaTranslation = 0.5;
	@Parameter(names = { "--lambdaLanguageModel", }, description = "lambda of languageModel")
	private double lambdaLanguageModel = 0.5;
	@Parameter(names = { "--histogramPruningLimit", }, description = "the max number of items each stack will hold")
	private int histogramPruningLimit = 10;

	public int histogramPruningLimit() {
		return histogramPruningLimit;
	}
	public double lambdaTranslation() {
		return lambdaTranslation;
	}
	public double lambdaLanguageModel() {
		return lambdaLanguageModel;
	}
	public String fileToTranslate() {
		return fileToTranslate;
	}
	public String phraseTableFile() {
		return phraseTableFile;
	}
	public String outputFile() {
		return outputFile;
	}
	public String modelFile() {
		return modelFile;
	}
	public String latticeFile() {
		return latticeFile;
	}
	public void histogramPruningLimit(int histogramPruningLimit) {
		this.histogramPruningLimit = histogramPruningLimit;
	}
	public void lambdaLanguageModel(double lambdaLanguageModel) {
		this.lambdaLanguageModel = lambdaLanguageModel;
	}
	public List<String> words() {
		return Splitter.on(" ").splitToList(sentence);
	}
	@Override
	public String toString() {
		return "TranslatorArgs [parameters=" + parameters + ", phraseTableFile=" + phraseTableFile + ", sentence="
				+ sentence + ", fileToTranslate=" + fileToTranslate + ", outputFile=" + outputFile + ", modelFile="
				+ modelFile + ", latticeFile=" + latticeFile + ", lambdaTranslation=" + lambdaTranslation
				+ ", lambdaLanguageModel=" + lambdaLanguageModel + ", histogramPruningLimit=" + histogramPruningLimit
				+ "]";
	}

	
}



