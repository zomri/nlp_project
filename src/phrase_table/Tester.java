package phrase_table;

import java.util.Map;
import java.util.Map.Entry;
import com.google.common.collect.Multiset;

//TODO - rename and remove comments

public class Tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String a3FileA = args[0];
		String a3FileB = args[1];
		
		PhraseTableCalculator ptc = new PhraseTableCalculator(a3FileA,a3FileB);

		//TODO - move all this code to PhraseTableCalculator - use Params to extract the args and pass them on..
//		List<A3SentenceContainer> sentencesA = new A3FilesReader().readSentenceAndAlignment(args[0]);
//		List<A3SentenceContainer> sentencesB = new A3FilesReader().readSentenceAndAlignment(args[1]);

		//fill those up later
		//	Set<String> engPhraseSet = Sets.newHashSet();
		Map<String,Multiset<String>> engGivenHebPhraseMap = ptc.calc(); 
				//Maps.newHashMap(); //format: eng_|_heb


//		for (int i=0; i<sentencesA.size(); ++i) {
//			System.out.println(i);
//			AlignmentMatrix am = new AlignedSentenceReader().createMatrix(sentencesA.get(i), sentencesB.get(i));
//			//TODO - make sure rows are source (HEB) and columns are target (ENG)
//
//			//TODO - run the article algorithm - find consistent phrases - put them in a table
//			// and calculate their frequency (I guess counting the singe/pairs of them)
//			// NOTICE - need to translate only from HEB to ENG! make sure to work with correct matrix dimention
//			//consistent - any matching word shouldbe included in the phrase (either source or target)
//			//iterate the source words - check if pair is consistent - if so - add it to counting set
//
//			//unaligned word - target word that has no source word (empty matrix column)
//			// - any easy way to mark such word? can we look only in adjacent cells?
//			//Q: what do we do with "source" words that don't have alignment at all?
//
//			/*
//			 * My idea for algorithm (not article): for each possible consecutive src phrase:
//			 *  - find "max" column of all words aligments
//			 *  - if we've included a target word, which aligment is not part of the source - it's not consistent
//			 *  - otherwise - it's good.
//			 *  (verify this is correct and run-time..)
//			 */
//
//
//			//int[][] mat = am.getAlignmentMatrix();
//			List<String> srcWords = am.getSrcWords();
//			List<String> targetWords = am.getTargetWords();
//
//			Map<Integer,Set<Integer>> src2targetAl = am.getSrc2targetAlign();
//			Map<Integer,Set<Integer>> target2srcAl = am.getTarget2srcAlign();
//
//			int numRows = srcWords.size();
//			//int numCols = mat[0].length;
//
//			for (int row1=0; row1<numRows; ++row1)
//			{
//				for (int row2=row1; row2<numRows; ++row2) {
//					//even if not consistent - still needed for freq calculation
//					String srcPhrase = getPhrase(srcWords,row1,row2);
//
//					//check if (row1->row2 is consistent with translation)
//					//return also the max aligned matrix - so we can calculate the phrases..
//					//TODO - perhaps return also list of non-aligned words at prefix/postfix of phrase..
//					List<Integer> minMaxIdx = getMaxConsistentIdx(srcWords,targetWords,src2targetAl,target2srcAl,row1,row2);
//					if (minMaxIdx != null) { //otherwise - not consistent!
//						//SHIT - what do we do with non-aligned words? (like "," in example)?
//
//						//TODO - iterate from min downwards, max upwards - collect possible words with 
//						// misaligend prefixes/postfixes
//						List<List<String>> nonAlignedPrefixPostix = getNonalignments(targetWords,target2srcAl,minMaxIdx.get(0),minMaxIdx.get(1));
//						String alignedPhrase = getPhrase(targetWords,minMaxIdx.get(0),minMaxIdx.get(1));						
//
//						List<String> possiblePhrases = getPossiblePhrases(alignedPhrase,nonAlignedPrefixPostix);
//
//						for (String possiblePhrase : possiblePhrases)
//						{
//							Multiset<String> engPhrases =  engGivenHebPhraseMap.get(srcPhrase);
//							if (engPhrases == null)
//							{
//								engPhrases = HashMultiset.create();
//								engPhrases.add(possiblePhrase);
//								engGivenHebPhraseMap.put(srcPhrase, engPhrases);
//							}
//							else
//							{
//								engPhrases.add(possiblePhrase); //TODO - verify this updates the inner hashmap..
//							}
//
//							//							Multiset<String> hebPhrases =  engGivenHebPhraseMap.get(possiblePhrase);
//							//							if (hebPhrases == null)
//							//							{
//							//								hebPhrases = HashMultiset.create();
//							//								hebPhrases.add(srcPhrase);
//							//								engGivenHebPhraseMap.put(possiblePhrase, hebPhrases);
//							//							}
//							//							else
//							//							{
//							//								hebPhrases.add(srcPhrase); //TODO - verify this updates the inner hashmap..
//							//							}
//						}
//						//engGivenHebPhraseSet.add(alignedPhrase + "_|_"+srcPhrase);
//
//					}
//				}
//			}
//		}
		
		//Out-Of-Mem when trying to fill map.
		//Instead - write entries directly to file.
		
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
