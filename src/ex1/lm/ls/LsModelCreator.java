package ex1.lm.ls;

import static ex1.common.Constants.UNK;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.beust.jcommander.internal.Sets;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import ex1.common.CorpusReader;
import ex1.common.Model;
import ex1.common.WordTuple;
import ex1.lm.LmArgs;
import ex1.lm.ModelCreator;

public class LsModelCreator implements ModelCreator {

	private LmArgs cliArgs;
	private Multiset<WordTuple> ngrams = HashMultiset.create();
	private Multiset<WordTuple> nMinusGrams = HashMultiset.create();
	private Set<String> vocabulary = Sets.newHashSet();

	public LsModelCreator(LmArgs cliArgs) {
		this.cliArgs = cliArgs;
		vocabulary.add(UNK);
	}

	public Model createModel() {
		// read file, sentence by sentence, and update maps
		CorpusReader corpusReader = new CorpusReader(cliArgs.inputfile(),
				new Predicate<List<String>>() {
					@Override
					public boolean apply(List<String> list) {
						List<String> listWithUnknows = getNewListWithUnknows(list);
						for (int i = 0; i < listWithUnknows.size()
								- cliArgs.n() + 1; i++) {
							List<String> subList = listWithUnknows.subList(i, i
									+ cliArgs.n());
							ngrams.add(new WordTuple(subList));
						}
						// loop for n-1grams
						for (int i = 0; i < listWithUnknows.size()
								- cliArgs.n() + 2; i++) {
							List<String> subList = listWithUnknows.subList(i, i
									+ cliArgs.n() - 1);
							nMinusGrams.add(new WordTuple(subList));
						}
						return true;
					}
				});
		corpusReader.read();

		Map<Integer, Multiset<WordTuple>> map = Maps.newHashMap();
		map.put(cliArgs.n(), ngrams);
		map.put(cliArgs.n() - 1, nMinusGrams);

		return new LsModel(cliArgs.n(), cliArgs.lambda(), "ls", map, vocabulary);

	}

	private List<String> getNewListWithUnknows(List<String> subList) {
		List<String> $ = Lists.newArrayList();
		for (String string : subList) {
			if (vocabulary.contains(string)) {
				$.add(string);
			} else {
				$.add(UNK);
				vocabulary.add(string);
			}
		}
		return $;
	}

	public int vocabularySize() {
		return vocabulary.size();
	}

}
