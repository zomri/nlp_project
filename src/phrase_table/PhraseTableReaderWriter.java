package phrase_table;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import edu.stanford.nlp.util.Pair;

public class PhraseTableReaderWriter {

	String filename;
	private final String delim = "__";
	BufferedWriter writer;
	
	
	public static void main(String[] args) {
		String phraseTableFilename = "d:/test_phrase_table.txt"; 
		PhraseTableReaderWriter ptrw = new PhraseTableReaderWriter(phraseTableFilename);
		ptrw.openFileForW();

		ptrw.write("sami","HASAMI",1.0);
		ptrw.write("thanks","TODA",2.4);
		ptrw.write("this is the nba","ZE HA NBA",0.542);
		
		ptrw.closeFile();
	}
	
	public PhraseTableReaderWriter(String filename) {
		this.filename = filename;
	}

	public void write(Map<String, Multiset<Pair<String, Double>>> map) {
		Charset charset = Charset.forName("UTF-8");	
		Path file = Paths.get(filename);

		try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {

			for (Entry<String, Multiset<Pair<String, Double>>> entry : map.entrySet())
			{
				for (Pair<String, Double> pair : entry.getValue()) 
				{
					String line = entry.getKey() + delim + pair.first + delim + pair.second.toString();
					writer.write(line,0,line.length());
				}


				//writer.write(s, 0, s.length());
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			throw new RuntimeException(x);
		}
	}

	public Map<String, Multiset<Pair<String, Double>>> read() {

		Map<String, Multiset<Pair<String, Double>>> map = Maps.newHashMap();

		Path file = Paths.get(filename);
		int i = 0;
		try (BufferedReader reader = Files.newBufferedReader(file)) {

			String line = "";
			while ((line = reader.readLine()) != null) 
			{
				Scanner s = new Scanner(line).useDelimiter(delim);
				String srcPhrase = s.next();
				String targetPhrase = s.next();
				Double p = s.nextDouble();
				s.close(); 

				Multiset<Pair<String, Double>> hashset =  map.get(srcPhrase);
				if (hashset == null)
				{
					hashset = HashMultiset.create();
					hashset.add(new Pair<String, Double>(targetPhrase,p));
					map.put(srcPhrase, hashset);
				}
				else
				{
					hashset.add(new Pair<String, Double>(targetPhrase,p));
				}
				i++;
				if (i%10000 == 0){
					System.out.println(i);
				}

			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			throw new RuntimeException(x);
		}

		return map;

	}

	public void openFileForW() {
		Charset charset = Charset.forName("UTF-8");	
		Path file = Paths.get(filename);

		try {
			this.writer = Files.newBufferedWriter(file,charset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		
	}

	public void write(String srcPhrase, String targetPhrase, Double p) {
		String line = srcPhrase + delim + targetPhrase + delim + p.toString()+"\n";
		try {
			writer.write(line,0,line.length());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	public void closeFile() {
		try {
			this.writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

}
