package translator;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.beust.jcommander.internal.Lists;

public class StackDecoderTest {

	private List<String> origin = Lists.newArrayList();
	private LatticePhraseTranslator phraseTranslator = new LatticePhraseTranslator();
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
		phraseTranslator.putTranslation("a", "b", 1);
		createdTested();
		List<String> translated = tested.translate();
		assertEquals(Lists.newArrayList("b", "b", "b"), translated);
	}
	@Test
	public void testHighestScoreTranslation() {
		origin.add("a");
		phraseTranslator.putTranslation("a", "b", 3);
		phraseTranslator.putTranslation("a", "c", 2);
		createdTested();
		List<String> translated = tested.translate();
		assertEquals(Lists.newArrayList("b"), translated);
	}
	@Test
	public void testRecombine() {
		origin.add("hambre");
		origin.add("tengo");
		phraseTranslator.putTranslation("tengo", "am", 100);
		phraseTranslator.putTranslation("hambre", "hungry", 103);
		phraseTranslator.putTranslation("hambre", "hunger", 102);
		createdTested();
		List<String> translated = tested.translate();
		assertEquals(Lists.newArrayList("hungry", "am"), translated);
	}
	private void createdTested() {
		tested = new StackDecoder(origin, phraseTranslator);
	}

	//test steps of few words
	//test with scoring algorithm from model
	//test prune
}
