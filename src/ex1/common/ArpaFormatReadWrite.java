package ex1.common;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.google.common.base.Splitter;
import common.TextFileUtils;

import ex1.eval.EvalArgs;
import ex1.lm.LmArgs;
import ex1.lm.ls.LsModel;
import ex1.lm.wb.WbModel;

/**
 * Writing model data to file in ARPA format.
 * 
 * @author zomri
 * 
 */
public class ArpaFormatReadWrite {

	private static final String SEPERATOR = " ";
	private static final String GRAMS = "-grams:";
	private static final String NGRAM = "ngram";
	private static final String DATA = "\\\\data\\\\";
	private static final Pattern pattern = Pattern.compile("\\\\\\\\(\\d+)"
			+ GRAMS);
	private static final String LAMBDA = "lambda";
	private static final String SMOOTH = "smooth";
	private Smooth smooth;

	public ArpaFormatReadWrite() {
	}

	public void writeToFile(LmArgs cliArgs, Model model) {
		// print header
		StringBuilder content = new StringBuilder();
		content.append(DATA + "\n");
		for (Entry<Integer, Map<WordTuple, WordTupleData>> entry : model
				.ngramsByCount().entrySet()) {
			content.append(NGRAM + entry.getKey() + "="
					+ entry.getValue().size() + "\n");
		}
		content.append(LAMBDA + "=" + cliArgs.lambda() + "\n");
		content.append(SMOOTH + "=" + cliArgs.smooth() + "\n");
		content.append("\n");
		// print ngrams
		for (Entry<Integer, Map<WordTuple, WordTupleData>> entry : model
				.ngramsByCount().entrySet()) {
			printNgrams(entry.getValue(), content, entry.getKey());
		}
		content.append("\n");
		TextFileUtils.setContents(cliArgs.outputFile(), content.toString());
	}

	private void printNgrams(Map<WordTuple, WordTupleData> ngrams,
			StringBuilder content, int n) {
		Map<WordTuple, WordTupleData> map = ngrams;
		content.append("\\\\" + n + GRAMS + "\n");
		for (Entry<WordTuple, WordTupleData> e : map.entrySet()) {
			content.append(e.getValue().p());
			for (String w : e.getKey().list()) {
				content.append(SEPERATOR + w);
			}
			content.append(SEPERATOR + e.getValue().count());
			content.append(SEPERATOR + e.getValue().countUniqWordsAfter());
			content.append("\n");
		}
	}
	
	public Model readFromFile(EvalArgs cliArgs) {
		return readFromFile(cliArgs.modelfile());
	}

	public Model readFromFile(String modelfile) {

		List<String> content = TextFileUtils.getContent(modelfile);
		Map<Integer, Integer> ngramsCount = Maps.newHashMap();
		Map<Integer, Map<WordTuple, WordTupleData>> ngrams = Maps.newHashMap();
		int n = 0;
		double lambda = 0;

		//TODO - read buffered - no need for all data to be loaded - memory!
//		Path file = Paths.get(modelfile);
//		Charset charset = Charset.forName("UTF8");
//		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
//		    String line = null;
//		    while ((line = reader.readLine()) != null) {
//		        System.out.println(line);
//		    }
//		} catch (IOException x) {
//		    System.err.format("IOException: %s%n", x);
//		}
		System.out.println("done reading " + new Date());
		System.out.println("now parsing lines " + content.size());
		for (int i = 0; i < content.size(); i++) {
				System.out.println(i);
			String line = content.get(i);
			if (line.startsWith(NGRAM)) {
				String[] split = line.split("=");
				Integer key = Integer
						.valueOf(split[0].substring(NGRAM.length()));
				Integer value = Integer.valueOf(split[1]);
				ngramsCount.put(key, value);
				continue;
			}
			if (line.startsWith(LAMBDA)) {
				String[] split = line.split("=");
				lambda = Double.valueOf(split[1]);
				continue;
			}
			if (line.startsWith(SMOOTH)) {
				String[] split = line.split("=");
				smooth = Smooth.valueOf(split[1]);
				continue;
			}
			Matcher matcher = pattern.matcher(line);
			if (matcher.matches()) {
				int key = Integer.valueOf(matcher.group(1));
				int count = ngramsCount.get(key);
				ngrams.put(key,
						parseNgrmas(Lists.newArrayList(content.subList(i + 1, i + 1 + count)), key));
				i += count;
				n = Math.max(n, key);
				continue;
			}
		}
		System.out.println(smooth + " done parsing " + new Date());
		switch (smooth) {
		case ls:
			return new LsModel(n, lambda, ngrams);
		case wb:
			return new WbModel(n, lambda, ngrams);
		default:
			throw new RuntimeException("no smooth impl " + smooth);
		}
	}

	private Map<WordTuple, WordTupleData> parseNgrmas(List<String> content,
			int ngram) {
		Map<WordTuple, WordTupleData> $ = Maps.newHashMap();
		int i = 0;
		System.out.println("going to work " + content.size());
		for (String line : content) {
			System.out.println(i++);
			List<String> split = Splitter.on(SEPERATOR).splitToList(line);
			WordTupleData wordTupleData = new WordTupleData(
					Double.valueOf(split.get(0)), Integer.valueOf(split
							.get(split.size() - 2)), Integer.valueOf(split
							.get(split.size() - 1)));
			WordTuple wordTuple = new WordTuple(Lists.newArrayList(split
					.subList(1, split.size() - 2)));
			$.put(wordTuple, wordTupleData);
		}
		return $;
	}

	public enum Smooth {
		ls, wb;
	}
}
