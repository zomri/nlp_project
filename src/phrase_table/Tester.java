package phrase_table;

import java.util.List;

public class Tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		List<A3SentenceContainer> sentencesA = new A3FilesReader().readSentenceAndAlignment(args[0]);
		List<A3SentenceContainer> sentencesB = new A3FilesReader().readSentenceAndAlignment(args[1]);
		
		AlignmentMatrix am = new AlignedSentenceReader().createMatrix(sentencesA.get(1), sentencesB.get(1));
	}

}
