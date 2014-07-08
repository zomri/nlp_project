package sentence_truncate;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.google.common.collect.Lists;
import common.ExceptionUtils;
import common.TextFileUtils;

public class Truncate {

	private final Logger log = Logger.getLogger(Truncate.class);
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
		TruncateArgs cliArgs = new TruncateArgs();
		new JCommander(cliArgs, args);
		List<List<String>> filesContent = Lists.newArrayList();
		int size = -1;
		for (String file : cliArgs.files()) {
			List<String> content = TextFileUtils.getContent(file);
			if (size == -1) {
				size = content.size();
			}
			else {
				if (size != content.size()) {
					throw new RuntimeException("file length not match " + size + " " + content.size());
				}
			}
			filesContent.add(content);
		}
		List<OutputStreamWriter> filesWriters = Lists.newArrayList();
		for (String file : cliArgs.files()) {
			filesWriters.add(TextFileUtils.getWriter(file + ".truncate"));
		}
		for (int i = 0; i < filesContent.get(0).size(); i++) {
			int j = i;
			if (filesContent.stream().anyMatch(content -> content.get(j).split(" ").length > cliArgs.maxTokens)) {
				//dropping line
			} else {
				for (int k = 0; k < filesWriters.size(); k++) {
					try {
						filesWriters.get(k).write(filesContent.get(k).get(i));
					} catch (IOException e) {
						throw ExceptionUtils.asUnchecked(e);
					}
				}
			}
		}
		
	}

}
