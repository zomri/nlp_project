package latticeGenerator;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import common.TextFileUtils;

import edu.stanford.nlp.util.Pair;

public class LaticeGeneratorFromFileReader {

	private Pair<Integer, Integer> span;
	private String latticeFilename;

	public LaticeGeneratorFromFileReader(String latticeFilename) {
		super();
		this.latticeFilename = latticeFilename;
	}

	public Map<Pair<Integer, Integer>, Multiset<Pair<List<String>, Double>>> readLattice() {
		final Map<Pair<Integer, Integer>, Multiset<Pair<List<String>, Double>>> $ = Maps.newHashMap();
		Predicate<String> linePredicate = new Predicate<String>() {
			@Override
			public boolean apply(String line) {
				if (line.endsWith(":")) {
					String patternString = "(\\d+)-(\\d+):";
					Pattern pattern = Pattern.compile(patternString);
					Matcher matcher = pattern.matcher(line);
					matcher.find();
					span = new Pair<Integer, Integer>(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher
							.group(2)));
					HashMultiset<Pair<List<String>,Double>> create = HashMultiset.create();
					$.put(span, create);
				} else {
					List<String> lineSplitted = Splitter.on("\t").splitToList(line);
					double prob = Double.parseDouble(lineSplitted.get(lineSplitted.size() - 1));
					List<String> theSentence = lineSplitted.subList(0, lineSplitted.size() - 1);
					$.get(span).add(new Pair<>(theSentence, prob));
				}
				return true;
			}
		};
		TextFileUtils.getContentByLines(latticeFilename, linePredicate);
		return $;

	}
}
