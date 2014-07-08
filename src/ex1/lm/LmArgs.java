package ex1.lm;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

public class LmArgs {
	@Parameter
	public List<String> parameters = Lists.newArrayList();
	@Parameter(names = { "-n", }, description = "n value")
	private Integer n = 1;
	@Parameter(names = { "-i", }, description = "input file")
	private String inputFile;
	@Parameter(names = { "-o", }, description = "output file")
	private String outputFile;
	@Parameter(names = { "-s", }, description = "smooth algorithm")
	private String smooth;
	@Parameter(names = { "-lmd", }, description = "lambda")
	private Double lambda = 1.0;

	public int n() {
		return n;
	}

	public String inputfile() {
		return inputFile;
	}

	public String smooth() {
		return smooth;
	}

	public String outputFile() {
		return outputFile;
	}

	public Double lambda() {
		return lambda;
	}

	@Override
	public String toString() {
		return "CliArgs [parameters=" + parameters + ", n=" + n
				+ ", inputFile=" + inputFile + ", outputFile=" + outputFile
				+ ", smooth=" + smooth + ", lambda=" + lambda + "]";
	}
}
