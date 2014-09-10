package latticeGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

import edu.stanford.nlp.util.Pair;
import phrase_table.PhraseTableReaderWriter;

public class LatticeGenerator {

	private String inputSentence;
	private String latticeFilename;
	private Map<String, Multiset<Pair<String, Double>>> phraseTableMap;
	
	public static void main(String[] args) {
		
		String input = "+W LK $WB L+ DRK +K M+ DBRH DM$Q W+ B+ HAT W+ M$XT AT XZAL L+ HMLK EL ARM";
		
		PhraseTableReaderWriter ptrw = new PhraseTableReaderWriter("d:\\phrase_table.txt");
		Map<String, Multiset<Pair<String, Double>>> map = ptrw.read();

		LatticeGenerator lg = new LatticeGenerator(input, map, "d:\\lattice.txt");

		lg.createLaticeFile();
	}

	public LatticeGenerator(String inputSentence, Map<String, Multiset<Pair<String, Double>>> map, String latticeFilename)
	{
		this.setInputSentence(inputSentence);
		this.setLatticeFilename(latticeFilename);
		this.setPhraseTableFilename(map);
	}

	private void setPhraseTableFilename(
			Map<String, Multiset<Pair<String, Double>>> map) {
		this.phraseTableMap = map;
		
	}

	/**
	 * 
	 * @param latticeFilename
	 * @return map where:
	 * Key == span lower bound, 
	 * Value is map - where Key is lower/upper bounds and value is enlish translation and score 
	 */
	public Map<Pair<Integer, Integer>, Multiset<Pair<String, Double>>> readLattice(String latticeFilename)
	{
		String patternString = "%d-%d:";
		Pattern pattern = Pattern.compile(patternString);
		
		Map<Pair<Integer, Integer>, Multiset<Pair<String, Double>>> map = Maps.newHashMap();
		
		//Can we read multiple lines - with different structure - to read file at once
		//if not - read line by line - for each "x-y:" line - read the translations and scores..
		Path file = Paths.get(latticeFilename);
		
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
		    String line = null;
		    Multiset<Pair<String, Double>> innerSet = null;
		    while ((line = reader.readLine()) != null) {
		        //If line in format "x-y:" - create new entry in map - insert following lines to that entry
				Matcher matcher = pattern.matcher(line.trim());
		    	if (matcher.matches()) 
		    	{
		    		Pattern pattern2 = Pattern.compile("%d");
		    		Matcher matcher2 = pattern2.matcher(line);
		    		matcher2.find();
		    		Integer from = Integer.valueOf(line.substring(matcher2.start(), matcher2.end()));
		    		matcher2.find();
		    		Integer to = Integer.valueOf(line.substring(matcher2.start(), matcher2.end()));
		    		
		    		Pair<Integer, Integer> pair = new Pair<Integer, Integer>(from, to);
		    		innerSet = HashMultiset.create();
		    		map.put(pair, innerSet);
		    	}
		    	else {
		    		String translation = "";
		    		Double prob = 0.0;
		    		
		    		//TODO - init data from line...
//		    		Pattern pattern2 = Pattern.compile("%s\t%u"); //%u is for double?
//		    		Matcher matcher2 = pattern2.matcher(line);
//		    		matcher2.find();
//		    		String text = line.substring(matcher2.start(), matcher2.end());
//		    		matcher2.find();
//		    		Integer to = Integer.valueOf(line.substring(matcher2.start(), matcher2.end()));

		    		
		    		innerSet.add(new Pair<String, Double>(translation,prob));
		    	}
		    	
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		
		return map;
		
	}
	
	public Map<Pair<Integer, Integer>, Multiset<Pair<String, Double>>> createLaticeFile()
	{

		Map<Pair<Integer, Integer>, Multiset<Pair<String, Double>>> map = Maps.newHashMap();
		
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
						Multiset<Pair<String, Double>> innerSet = HashMultiset.create();
			    		map.put(rangePair, innerSet);
						
						for (Pair<String, Double> pair : set)
						{
							writer.write(pair.first+"\t"+pair.second+"\n");
							innerSet.add(new Pair<String,Double>(pair.first,pair.second));
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
