package translator;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

public class TranslatorArgs {

	@Parameter
	public List<String> parameters = Lists.newArrayList();
	@Parameter(names = { "-p", }, description = "phrase table file")
	private String phraseTableFile;
	@Parameter(names = { "-m", }, description = "model file")
	private String modelFile;

	public String phraseTableFile() {
		return phraseTableFile;
	}

	public String modelFile() {
		return modelFile;
	}

}



