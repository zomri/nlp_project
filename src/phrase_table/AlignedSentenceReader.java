package phrase_table;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Reads s->t and t->s alignment from A3 files - creates alignment matrix out of it 
 * 
 * @author zomri
 *
 */
public class AlignedSentenceReader {

	public AlignmentMatrix createMatrix(A3SentenceContainer a2b, A3SentenceContainer b2a) {

		//a is "rows", b is "columns"
		//a2b.dst - words of b (and vice versa)
		//a2b.srcAlignment - a words + alignment to b

		AlignmentMatrix am = new AlignmentMatrix();
		//int[][] matrix = new int[a2b.getSrcNumWords()+1][b2a.getSrcNumWords()+1]; //+1 for NULL word

	
		//Currently ignore NULL entry and empty entries 
		Pattern p = Pattern.compile(" ");
		List<String> bWords = Arrays.asList(p.split(a2b.getDst()));
		List<String> aWords = Arrays.asList(p.split(b2a.getDst()));

		Map<Integer,Set<Integer>> src2targetAlign = initAlignMapMaps(aWords.size());
		Map<Integer,Set<Integer>> target2srcAlign = initAlignMapMaps(bWords.size());

		//example: " Also ({ 1 }) for ({ }) "

		//Scanner scan = new Scanner(ab.getSrcAlignment());
		//scan.findInLine
		//All ({ 1 })
		p = Pattern.compile("([\\S]+ \\(\\{[\\d\\s]*\\}\\))");
		Matcher m = p.matcher(a2b.getSrcAlignment()) ;  

		int idx = 0;
		while (m.find()) {
			String token = m.group();
			//System.out.println("item = `" + m.group() + "`");
			Scanner sc = new Scanner(token);
			String word = sc.next("\\S*");

			//Ignoring NULL word - don't think we need it
			if (!word.equals("NULL")) {
				while (sc.hasNext()) {
					if (sc.hasNextInt()) {
						int alignIdx =sc.nextInt(); 
						//alignments.add(alignIdx-1); //-1 = indexing start at 0..
						src2targetAlign.get(idx).add(alignIdx-1);
						target2srcAlign.get(alignIdx-1).add(idx);
						//matrix[idx][alignIdx] = 1;
						
					}
					sc.next();
				}
				//src2targetAlign.put(idx, alignments);
				idx++;
			}
			
		}

		m = p.matcher(b2a.getSrcAlignment()) ;  
		idx = 0;
		while (m.find()) {
			String token = m.group();
			//System.out.println("item = `" + m.group() + "`");
			Scanner sc = new Scanner(token);
			String word = sc.next("\\S*");
			//Ignoring NULL word - don't think we need it
			if (!word.equals("NULL")) {
				while (sc.hasNext()) {
					if (sc.hasNextInt()) {
						int alignIdx =sc.nextInt(); 
						//alignments.add(alignIdx-1); //-1 = indexing start at 0..
						//matrix[alignIdx][idx] = 1;
						target2srcAlign.get(idx).add(alignIdx-1);
						src2targetAlign.get(alignIdx-1).add(idx);
						
					}
					sc.next();
				}
				//target2srcAlign.put(idx, alignments);
				idx++;
			}
			
		}
	
		am.setSrc2TargetAlignment(src2targetAlign);
		am.setTarget2SrcAlignment(target2srcAlign);
		am.setSrcWords(aWords);
		am.setTargetWords(bWords);
		return am;
	}

	private Map<Integer, Set<Integer>> initAlignMapMaps(int size) {
		// add all integers between [0,size] and create empty set
		Map<Integer, Set<Integer>> res = Maps.newHashMap();
		
		for (int i=0; i<size; ++i) 
		{
			res.put(i, Sets.<Integer>newTreeSet()); //TreeSet - sorted..
		}
		
		return res;
	}

}
