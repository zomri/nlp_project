package language_model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.beust.jcommander.JCommander;

import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.util.StringUtils;
import ex1.lm.LmArgs;

public class EnglishTokenizer {

	public static void main(String[] args)
	{
		EngTokArgs cliArgs = new EngTokArgs();
		JCommander jCommander = new JCommander(cliArgs, args);
		
		DocumentPreprocessor dp = new DocumentPreprocessor(cliArgs.inputfile());
		Charset charset = Charset.forName("UTF-8");
		
		Path file = Paths.get(cliArgs.outputFile());
		
		try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
		    int counter = 1;
			for (List sentence : dp) {
				//System.out.println(StringUtils.join(sentence));
				if (!sentence.isEmpty())
				{
					System.out.println(counter++);
					writer.write(StringUtils.join(sentence));
					writer.write(System.lineSeparator());
					//System.out.println(counter++);
				}
				
				//writer.write(sentence, 0, sentence.length());
				//TODO - write sentences to file - line by line.
				
			}     
			writer.close();
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		

	}
}
