package sentence_truncate;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

public class TruncateArgs {
	@Parameter
	public List<String> parameters = Lists.newArrayList();
	@Parameter(names = { "-n", }, description = "number fo tokens")
	public Integer maxTokens = 60;

	public List<String> files() {
		return parameters;
	}
}
