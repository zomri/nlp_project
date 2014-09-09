package latticeGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multiset;

import phrase_table.PhraseTableReaderWriter;
import phrase_table.StringDoublePair;

public class LatticeGenerator {

	private String inputSentence;
	private String latticeFilename;
	private Map<String, Multiset<StringDoublePair>> phraseTableMap;
	
	public static void main(String[] args) {
		
		String input = "+W LK $WB L+ DRK +K M+ DBRH DM$Q W+ B+ HAT W+ M$XT AT XZAL L+ HMLK EL ARM";
		
		PhraseTableReaderWriter ptrw = new PhraseTableReaderWriter("d:\\phrase_table.txt");
		Map<String, Multiset<StringDoublePair>> map = ptrw.read();

		LatticeGenerator lg = new LatticeGenerator(input, map, "d:\\lattice.txt");

		lg.createLaticeFile();
	}

	public LatticeGenerator(String inputSentence, Map<String, Multiset<StringDoublePair>> map, String latticeFilename)
	{
		this.setInputSentence(inputSentence);
		this.setLatticeFilename(latticeFilename);
		this.setPhraseTableFilename(map);
	}

	private void setPhraseTableFilename(
			Map<String, Multiset<StringDoublePair>> map) {
		this.phraseTableMap = map;
		
	}

	public void createLaticeFile()
	{
		//PhraseTableReaderWriter ptrw = new PhraseTableReaderWriter(phraseTableFilename);
		//Map<String, Multiset<StringDoublePair>> map = ptrw.read();

		Charset charset = Charset.forName("UTF-8");	
		Path file = Paths.get(latticeFilename);
		BufferedWriter writer = null;
		try {
			writer = Files.newBufferedWriter(file,charset);

			//Foreach span of the sentence - find the relevant translations - and their scores (not in particular order)
			String[] words = inputSentence.split("\\s");

			for (int i=0; i<words.length; ++i)
			{
				String phrase = words[i];
				for (int j = i; j<words.length; ++j)
				{
					if(j!=i)
					{
						phrase += " "+ words[j];
					}


					Multiset<StringDoublePair> set = phraseTableMap.get(phrase);
					if (set != null)
					{
						writer.write(new Integer(i+1).toString()+"-"+new Integer(j+1).toString()+":\n");

						for (StringDoublePair pair : set)
						{
							writer.write(pair.getS()+"\t"+pair.getD()+"\n");
						}
					}
					else
					{
						System.out.println("Failed to find translation for '"+phrase+"'");
					}
				}
			}
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public String getInputSentence() {
		return inputSentence;
	}

	public void setInputSentence(String inputSentence) {
		this.inputSentence = inputSentence;
	}

	public String getLatticeFilename() {
		return latticeFilename;
	}

	public void setLatticeFilename(String latticeFilename) {
		this.latticeFilename = latticeFilename;
	}

}
