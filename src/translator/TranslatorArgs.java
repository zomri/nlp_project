package translator;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

public class TranslatorArgs {

	@Parameter
	public List<String> parameters = Lists.newArrayList();
	@Parameter(names = { "-p", }, description = "phrase table file")
	private String phraseTableFile;
	@Parameter(names = { "-i", }, description = "input file to translate")
	private String fileToTranslate;
	@Parameter(names = { "-o", }, description = "output file (if empty will print to stdout")
	private String outputFile;
	@Parameter(names = { "-m", }, description = "model file")
	private String modelFile;
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
	public void histogramPruningLimit(int histogramPruningLimit) {
		this.histogramPruningLimit = histogramPruningLimit;
	}
	@Override
	public String toString() {
		return "TranslatorArgs [" + (phraseTableFile != null ? "phraseTableFile=" + phraseTableFile + ", " : "")
				+ (fileToTranslate != null ? "fileToTranslate=" + fileToTranslate + ", " : "")
				+ (outputFile != null ? "outputFile=" + outputFile + ", " : "")
				+ (modelFile != null ? "modelFile=" + modelFile + ", " : "") + "lambdaTranslation=" + lambdaTranslation
				+ ", lambdaLanguageModel=" + lambdaLanguageModel + ", histogramPruningLimit=" + histogramPruningLimit
				+ "]";
	}

	
}



