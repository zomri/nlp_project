package translator;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.beust.jcommander.internal.Lists;

public class StackDecoderTest {

	private List<String> origin = Lists.newArrayList();
	private PhraseTranslator phraseTranslator = new PhraseTranslator();
	private StackDecoder tested;
	
	@Test
	public void testEmpty() {
		createdTested();
		List<String> translated = tested.translate();
		assertEquals(Lists.newArrayList(), translated);
	}
	@Test
	public void testVerySimpleTranslation() {
		origin.add("a");
		origin.add("a");
		origin.add("a");
		phraseTranslator.putTranslation("a", "b");
		createdTested();
		List<String> translated = tested.translate();
		assertEquals(Lists.newArrayList("b", "b", "b"), translated);
	}
	private void createdTested() {
		tested = new StackDecoder(origin, phraseTranslator);
	}

	//test steps of few words
	//test multiple translations
	//test with score
	//test prune and recombine
}
