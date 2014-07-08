package ex1.eval;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

public class EvalArgs {
	@Parameter
	public List<String> parameters = Lists.newArrayList();
	@Parameter(names = { "-i", }, description = "input file")
	private String inputFile;
	@Parameter(names = { "-m", }, description = "model file")
	private String modelFile;

	public String inputfile() {
		return inputFile;
	}

	public String modelfile() {
		return modelFile;
	}

}
