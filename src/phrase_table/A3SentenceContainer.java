package phrase_table;

public class A3SentenceContainer {

	private String srcAlignment;
	private String dst;
	private int srcNumWords;
	private int dstNumWords;
	
	public A3SentenceContainer(String headline, String dst, String srcAlignment) {
		this.dst = dst;
		this.srcAlignment =  srcAlignment;
		
		//Get number of src/dst words by paring headline "source length 37 target length 20 alignment"
		//Old school find...
		srcNumWords = Integer.valueOf(headline.substring(headline.indexOf("source length")+13, headline.indexOf("target")).trim());
		dstNumWords = Integer.valueOf(headline.substring(headline.indexOf("target length")+13, headline.indexOf("alignment")).trim());
	}
	
	public String getDst() {
		return dst;
	}
	
	public String getSrcAlignment() {
		return srcAlignment;
	}

	public int getSrcNumWords() {
		// TODO Auto-generated method stub
		return srcNumWords;
	}

	public int getDstNumWords() {
		// TODO Auto-generated method stub
		return dstNumWords;
	}
}
