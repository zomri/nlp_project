package translator;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;


public class LineReader {

	private Predicate<List<String>> linePredicate;
	private String line;

	public LineReader(String line, Predicate<List<String>> linePredicate) {
		this.line = line;
		this.linePredicate = linePredicate;
	}

	public void read() {
		List<String> list = Splitter.on(" ").omitEmptyStrings()
				.splitToList("<s> " + line.replace("Ó", " ") + " </s>");
		linePredicate.apply(list);
	}
}







