package phrase_table;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

public class PhraseTableCalculator {

	private String a3FileA;
	private String a3FileB;

	public PhraseTableCalculator(String a3FileA, String a3FileB) {
		this.a3FileA = a3FileA;
		this.a3FileB = a3FileB;
		
	}
	
	public Map<String,Multiset<String>> calc()
	{
		List<A3SentenceContainer> sentencesA = new A3FilesReader().readSentenceAndAlignment(a3FileA);
		List<A3SentenceContainer> sentencesB = new A3FilesReader().readSentenceAndAlignment(a3FileB);

		//fill those up later
		//	Set<String> engPhraseSet = Sets.newHashSet();
		Map<String,Multiset<String>> engGivenHebPhraseMap = Maps.newHashMap(); //format: eng_|_heb


		for (int i=0; i<sentencesA.size(); ++i) {
			System.out.println(i);
			AlignmentMatrix am = new AlignedSentenceReader().createMatrix(sentencesA.get(i), sentencesB.get(i));
			//make sure rows are source (HEB) and columns are target (ENG)

			/*
			 * My idea for algorithm (not article): for each possible consecutive src phrase:
			 *  - find "max" column of all words aligments
			 *  - if we've included a target word, which aligment is not part of the source - it's not consistent
			 *  - otherwise - it's good.
			 *  (verify this is correct and run-time..)
			 */


			//int[][] mat = am.getAlignmentMatrix();
			List<String> srcWords = am.getSrcWords();
			List<String> targetWords = am.getTargetWords();

			Map<Integer,Set<Integer>> src2targetAl = am.getSrc2targetAlign();
			Map<Integer,Set<Integer>> target2srcAl = am.getTarget2srcAlign();

			int numRows = srcWords.size();
			//int numCols = mat[0].length;

			for (int row1=0; row1<numRows; ++row1)
			{
				for (int row2=row1; row2<numRows; ++row2) {
					//even if not consistent - still needed for freq calculation
					String srcPhrase = getPhrase(srcWords,row1,row2);

					//check if (row1->row2 is consistent with translation)
					//return also the max aligned matrix - so we can calculate the phrases..
					List<Integer> minMaxIdx = getMaxConsistentIdx(srcWords,targetWords,src2targetAl,target2srcAl,row1,row2);
					if (minMaxIdx != null) { //otherwise - not consistent!
						// misaligend prefixes/postfixes
						List<List<String>> nonAlignedPrefixPostix = getNonalignments(targetWords,target2srcAl,minMaxIdx.get(0),minMaxIdx.get(1));
						String alignedPhrase = getPhrase(targetWords,minMaxIdx.get(0),minMaxIdx.get(1));						

						List<String> possiblePhrases = getPossiblePhrases(alignedPhrase,nonAlignedPrefixPostix);

						for (String possiblePhrase : possiblePhrases)
						{
							Multiset<String> engPhrases =  engGivenHebPhraseMap.get(srcPhrase);
							if (engPhrases == null)
							{
								engPhrases = HashMultiset.create();
								engPhrases.add(possiblePhrase);
								engGivenHebPhraseMap.put(srcPhrase, engPhrases);
							}
							else
							{
								engPhrases.add(possiblePhrase); 
							}
						}
					}
				}
			}
		}
		
		return engGivenHebPhraseMap;
	}
	
	private static List<String> getPossiblePhrases(String alignedPhrase,
			List<List<String>> misAlignedPrefixPostix) {

		List<String> prefixes = misAlignedPrefixPostix.get(0);
		List<String> postfixes = misAlignedPrefixPostix.get(1);

		List<String> res = Lists.newArrayList();

		String prefixStr = "";
		String postfixStr = "";

		for (String pref : prefixes)
		{
			prefixStr = (pref + " " + prefixStr).trim();
			for (String post : postfixes) 
			{
				postfixStr = (postfixStr + " " + post).trim();
				res.add((prefixStr+ " "+ alignedPhrase + " "+ postfixStr).trim());
			}
		}

		return res;
	}
	
	private static List<List<String>> getNonalignments(	List<String> targetWords, Map<Integer, Set<Integer>> target2srcAl, int minIdx, int maxIdx) {

		List<List<String>> res = Lists.newArrayList();
		List<String> prefixIdx = Lists.newArrayList();
		List<String> suffixIdx = Lists.newArrayList();

		prefixIdx.add(""); //empty prefix - needed when building all possible pref+phrase+post
		suffixIdx.add(""); //empty suffix - needed when building all possible pref+phrase+post

		//Find prefixes (min--)
		int i= minIdx-1;
		Set<Integer> alignments = target2srcAl.get(i);
		while (i >=0 && ((alignments == null) || alignments.isEmpty()))
		{
			prefixIdx.add(targetWords.get(i));
			alignments = target2srcAl.get(--i);
		}

		//Find suffixes (max++)
		i = maxIdx+1;
		alignments = target2srcAl.get(i);
		while (i < targetWords.size() && ((alignments == null) || alignments.isEmpty()))
		{
			suffixIdx.add(targetWords.get(i));
			alignments = target2srcAl.get(++i);
		}

		res.add(prefixIdx);
		res.add(suffixIdx);

		return res;
	}

	private static String getPhrase(List<String> words,
			int min, int max) {

		StringBuilder sb = new StringBuilder();

		for (String s : words.subList(min, max+1))
		{
			sb.append(s);
			sb.append(" ");
		}

		return sb.toString().trim();
	}

	//return min/max aligned indeces in List
	private static List<Integer> getMaxConsistentIdx(List<String> srcWords,
			List<String> targetWords, Map<Integer, Set<Integer>> src2targetAl,
			Map<Integer, Set<Integer>> target2srcAl, int row1, int row2) {
		//find min/max aligned idx - iterate all target words in between - chck that their aligment (if exists) 
		//is within the [row1 row2] interval

		//1st - find min/max aligments indeces

		Integer min = targetWords.size()+1;
		Integer max = -1;
		for (int i=row1; i<row2+1; ++i) {
			Set<Integer> aligments = src2targetAl.get(i);
			if (aligments != null && !aligments.isEmpty())
			{
				for (Integer alignIdx : aligments)
				{
					if (alignIdx < min)
						min = alignIdx;
					if (alignIdx > max)
						max = alignIdx;
				}
			}
		}

		//meanwhile - if there's no alignemnt at all - return null
		if (max == -1) {
			return null;
		}

		for (int i=min; i<max+1; ++i) {
			Set<Integer> aligments = target2srcAl.get(i);
			if (aligments != null && !aligments.isEmpty())
			{
				for (Integer alignIdx : aligments)
				{
					if (alignIdx < row1 || alignIdx > row2)
						return null;
				}
			}
		}



		List<Integer> res = Lists.newArrayList();
		res.add(min); res.add(max);
		return res;
	}

}
