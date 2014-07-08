package ex1.common;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import common.TextFileUtils;

public class CorpusReader {

	private Predicate<List<String>> linePredicate;
	private String inputfile;

	public CorpusReader(String inputfile, Predicate<List<String>> linePredicate) {
		this.inputfile = inputfile;
		this.linePredicate = linePredicate;
	}

	public void read() {
		TextFileUtils.getContentByLines(inputfile, new Predicate<String>() {
			@Override
			public boolean apply(String line) {
				List<String> list = Splitter.on(" ").omitEmptyStrings()
						.splitToList("<s> " + line.replace("Ó", " ") + " </s>");
				return linePredicate.apply(list);
			}

		});
	}

}
