package ex1.lm.wb;

import static ex1.common.Constants.UNK;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import ex1.common.CorpusReader;
import ex1.common.Model;
import ex1.common.WordTuple;
import ex1.lm.LmArgs;
import ex1.lm.ModelCreator;

public class WbModelCreator implements ModelCreator {

	private LmArgs cliArgs;
	private Map<Integer, Multimap<WordTuple, String>> ngrams = Maps
			.newHashMap();
	private Set<String> vocabulary = Sets.newHashSet();
	private Set<String> vocabularyWithUNK = Sets.newHashSet();

	public WbModelCreator(LmArgs cliArgs) {
		this.cliArgs = cliArgs;
		vocabulary.add(UNK);
		vocabularyWithUNK.add(UNK);
	}

	public Model createModel() {
		// read file, sentence by sentence, and update maps
		CorpusReader corpusReader = new CorpusReader(cliArgs.inputfile(),
				new Predicate<List<String>>() {
					@Override
					public boolean apply(List<String> list) {
						List<String> listWithUnknows = getNewListWithUnknows(list);
						for (int j = cliArgs.n(); j > 0; j--) {

							List<String> paddedList = Lists.newArrayList();
							paddedList.addAll(listWithUnknows);
							if (j > 1) { // Adding padding at start and end of
											// line, so number of ngrams is
											// equal for each n
								for (int k = 0; k < j - 1; paddedList
										.add("</s>"), k++)
									;
							}

							Multimap<WordTuple, String> ngramsForJ = getNgrams(j);
							for (int i = 0; i < listWithUnknows.size(); i++) {
								List<String> subList = paddedList.subList(i, i
										+ j);
								String wordAfter = UNK;
								if (paddedList.size() > i + j) {
									wordAfter = paddedList.get(i + j);
								}
								ngramsForJ.put(new WordTuple(subList),
										wordAfter);
							}
						}
						return true;
					}

				});
		corpusReader.read();

		return new WbModel(cliArgs.n(), cliArgs.lambda(), "wb", ngrams,
				vocabularyWithUNK);

	}

	private Multimap<WordTuple, String> getNgrams(int j) {
		Multimap<WordTuple, String> multimap = ngrams.get(j);
		if (null == multimap) {
			multimap = ArrayListMultimap.create();
			ngrams.put(j, multimap);
		}
		return multimap;
	}

	private List<String> getNewListWithUnknows(List<String> subList) {
		List<String> $ = Lists.newArrayList();
		for (String string : subList) {
			if (vocabulary.contains(string)) {
				$.add(string);
				vocabularyWithUNK.add(string);
			} else {
				$.add(UNK);
				vocabulary.add(string);
			}
		}
		return $;
	}

	public int vocabularySize() {
		return vocabularyWithUNK.size();
	}

}
