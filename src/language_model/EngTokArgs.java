package language_model;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

public class EngTokArgs {
	@Parameter
	public List<String> parameters = Lists.newArrayList();
	@Parameter(names = { "-i", }, description = "input file")
	private String inputFile;
	@Parameter(names = { "-o", }, description = "output file")
	private String outputFile;

	public String inputfile() {
		return inputFile;
	}

	public String outputFile() {
		return outputFile;
	}
	@Override
	public String toString() {
		return "CliArgs [parameters=" + parameters + ", inputFile=" + inputFile + 
				", outputFile=" + outputFile + "]";
	}
}
