package translator;

import java.util.List;
import java.util.Map;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;

public class PhraseTranslator {

	private Map<List<String>, List<String>> mapping = Maps.newHashMap();
	
	public void putTranslation(String key, String value) {
		mapping.put(Lists.newArrayList(key), Lists.newArrayList(value));
	}

	public List<String> getTranslation(List<String> words) {
		return mapping.get(words);
	}

}
