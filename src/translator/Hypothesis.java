package translator;

import java.util.List;

import com.beust.jcommander.internal.Lists;

public class Hypothesis {

	List<Hypothesis> prev = Lists.newArrayList();
	List<Hypothesis> next = Lists.newArrayList();
	List<String> words = Lists.newArrayList();
	List<Boolean> coverage = Lists.newArrayList();
	double score;
}
