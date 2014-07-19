package phrase_table;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		int[][] matrix = new int[a2b.getSrcNumWords()+1][b2a.getSrcNumWords()+1]; //+1 for NULL word

		//TODO - read A3 sentence, fill matrix according to alignment
		//Currently ignore NULL entry and empty entries 
		Pattern p = Pattern.compile(" ");
		List<String> bWords = Arrays.asList(p.split(a2b.getDst()));
		List<String> aWords = Arrays.asList(p.split(b2a.getDst()));


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
			while (sc.hasNext()) {
				if (sc.hasNextInt()) {
					int alignIdx =sc.nextInt(); 
					matrix[idx][alignIdx] = 1;
				}
				sc.next();
			}
			idx++;
		}

		m = p.matcher(b2a.getSrcAlignment()) ;  
		idx = 0;
		while (m.find()) {
			String token = m.group();
			//System.out.println("item = `" + m.group() + "`");
			Scanner sc = new Scanner(token);
			String word = sc.next("\\S*");
			while (sc.hasNext()) {
				if (sc.hasNextInt()) {
					int alignIdx =sc.nextInt(); 
					matrix[alignIdx][idx] = 1;
				}
				sc.next();
			}
			idx++;
		}
		//String [] abSrcAlignmentWords = p.split(ab.getSrcAlignment());
		//TODO- parse each slignment string (word and its corresponding indexes)
		//using Scanner

		am.setAlignmentMatrix(matrix);
		am.setSrcWords(aWords);
		am.setTargetWords(bWords);
		return am;
	}

}
