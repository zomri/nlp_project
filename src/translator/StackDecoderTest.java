package translator;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.beust.jcommander.internal.Lists;

public class StackDecoderTest {

	private List<String> origin = Lists.newArrayList();
	private LatticePhraseTranslator phraseTranslator = new LatticePhraseTranslator();
	private StackDecoder tested;
	
	@Before
	public void before() {
		createdTested();
	}
	
	@Test
	public void testEmpty() {
		assertEquals(Lists.newArrayList(), tested.translate());
	}
	@Test
	public void testVerySimpleTranslation() {
		origin.add("a");
		origin.add("a");
		origin.add("a");
		phraseTranslator.putTranslation("a", "b", 1);
		assertEquals(Lists.newArrayList("b", "b", "b"), tested.translate());
	}
	@Test
	public void testWordWithoutTranslation() {
		origin.add("a");
		assertEquals(Lists.newArrayList(StackDecoder.NA), tested.translate());
	}
	@Test
	public void test2WordsWithoutTranslation() {
		origin.add("a");
		origin.add("a");
		assertEquals(Lists.newArrayList(StackDecoder.NA, StackDecoder.NA), tested.translate());
	}
	@Test
	public void testHighestScoreTranslation() {
		origin.add("a");
		phraseTranslator.putTranslation("a", "b", 3);
		phraseTranslator.putTranslation("a", "c", 2);
		assertEquals(Lists.newArrayList("b"), tested.translate());
	}
	@Test
	public void testRecombine() {
		origin.add("hambre");
		origin.add("tengo");
		phraseTranslator.putTranslation("tengo", "am", 100);
		phraseTranslator.putTranslation("hambre", "hungry", 103);
		phraseTranslator.putTranslation("hambre", "hunger", 102);
		assertEquals(Lists.newArrayList("hungry", "am"), tested.translate());
	}
	@Test
	public void testMultiSteps() {
		origin.add("a");
		origin.add("b");
		phraseTranslator.putTranslation("a", "aa", 1);
		phraseTranslator.putTranslation("b", "bb", 1);
		phraseTranslator.putTranslation(Lists.newArrayList("a", "b"), Lists.newArrayList("ab"), 100);
		assertEquals(Lists.newArrayList("ab"), tested.translate());
	}
	private void createdTested() {
		TranslatorArgs cliArgs = new TranslatorArgs();
		cliArgs.histogramPruningLimit(3);
		tested = new StackDecoder(origin, phraseTranslator,null, cliArgs);
	}

	//test with scoring algorithm from model
	//test prune
}
