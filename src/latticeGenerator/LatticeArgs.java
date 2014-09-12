package latticeGenerator;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

public class LatticeArgs {

	@Parameter
	public List<String> parameters = Lists.newArrayList();
	@Parameter(names = { "-i", }, description = "input sentence")
	private String input;
	@Parameter(names = { "-pt", }, description = "phrase table file")
	private String phraseTableFile;
	@Parameter(names = { "-o", }, description = "output file")
	private String outputFile;
	
	public String input() {
		return input;
	}
	public String phraseTableFile() {
		return phraseTableFile;
	}
	public String outputFile() {
		return outputFile;
	}
	
}



