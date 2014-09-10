package latticeGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import phrase_table.PhraseTableReaderWriter;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

import edu.stanford.nlp.util.Pair;

public class LatticeGeneratorFileWriter {

	private String inputSentence;
	private String latticeFilename;
	private Map<String, Multiset<Pair<String, Double>>> phraseTableMap;
	
	public static void main(String[] args) {
		
		String input = "+W LK $WB L+ DRK +K M+ DBRH DM$Q W+ B+ HAT W+ M$XT AT XZAL L+ HMLK EL ARM";
		
		PhraseTableReaderWriter ptrw = new PhraseTableReaderWriter("d:\\phrase_table.txt");
		Map<String, Multiset<Pair<String, Double>>> map = ptrw.read();

		LatticeGeneratorFileWriter lg = new LatticeGeneratorFileWriter(input, map, "d:\\lattice.txt");

		lg.createLaticeFile();
	}

	public LatticeGeneratorFileWriter(String inputSentence, Map<String, Multiset<Pair<String, Double>>> map, String latticeFilename)
	{
		this.setInputSentence(inputSentence);
		this.setLatticeFilename(latticeFilename);
		this.setPhraseTableFilename(map);
	}

	private void setPhraseTableFilename(
			Map<String, Multiset<Pair<String, Double>>> map) {
		this.phraseTableMap = map;
		
	}

	
	
	public Map<Pair<Integer, Integer>, Multiset<Pair<List<String>, Double>>> createLaticeFile()
	{

		Map<Pair<Integer, Integer>, Multiset<Pair<List<String>, Double>>> map = Maps.newHashMap();
		
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


					Multiset<Pair<String, Double>> set = phraseTableMap.get(phrase);
					if (set != null)
					{
						writer.write(new Integer(i+1).toString()+"-"+new Integer(j+1).toString()+":\n");

						Pair<Integer, Integer> rangePair = new Pair<Integer, Integer>(i+1, j+1);
						Multiset<Pair<List<String>, Double>> innerSet = HashMultiset.create();
			    		map.put(rangePair, innerSet);
						
						for (Pair<String, Double> pair : set)
						{
							writer.write(pair.first+"\t"+pair.second+"\n");
							innerSet.add(new Pair<List<String>,Double>(Splitter.on(" ").splitToList(pair.first),pair.second));
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

		return map;
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
