package translator;

import java.util.Collection;
import java.util.List;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class LatticePhraseTranslator {

	private Multimap<List<String>, TranslationWithScore> mapping = HashMultimap.create();
	
	public void putTranslation(String key, String value, double score) {
		mapping.put(Lists.newArrayList(key), new TranslationWithScore(value, score));
	}

	public Collection<TranslationWithScore> getTranslation(List<String> words) {
		return mapping.get(words);
	}

}
