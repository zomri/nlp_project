package phrase_table;

import java.util.Map;
import java.util.Map.Entry;
import com.google.common.collect.Multiset;

//TODO - rename and remove comments

public class Tester {

	//important - parmeters: bible.eng.truncate_bible.heb.truncate.A3 bible.heb.truncate_bible.eng.truncate.A3 "d:\phrase_table.txt"
	public static void main(String[] args) {
		
		String a3FileA = args[0];
		String a3FileB = args[1];
		
		PhraseTableCalculator ptc = new PhraseTableCalculator(a3FileA,a3FileB);
		Map<String,Multiset<String>> engGivenHebPhraseMap = ptc.calc(); 
		
		System.out.println("Writing phrase table...");
		
		String phraseTableFilename = args[2]; 
		PhraseTableReaderWriter ptrw = new PhraseTableReaderWriter(phraseTableFilename);
		ptrw.openFileForW();

		int counter =0;
		for (Entry<String, Multiset<String>> entry: engGivenHebPhraseMap.entrySet()) {
			String hebPhrase = entry.getKey();
			Double totalCount = (double)entry.getValue().size(); //TODO - make sure this count multiples!
			for (String engPhrase: entry.getValue().elementSet()) { //TODO - why iterate same word twice?
				Double p = Math.log((double)entry.getValue().count(engPhrase)/totalCount);

				//System.out.println(engPhrase + "_|_"+hebPhrase+" == "+p.toString());

				ptrw.write(hebPhrase,engPhrase,p);
			}
			
			System.out.println(counter++);
		}
		ptrw.closeFile();
	}


}
