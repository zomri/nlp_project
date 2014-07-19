package phrase_table;

import java.util.List;

public class AlignmentMatrix {

	List<String> srcWords;
	List<String> targetWords;
	int[][] alignmentMatrix;

	
	public List<String> getSrcWords() {
		return srcWords;
	}
	public void setSrcWords(List<String> srcWords) {
		this.srcWords = srcWords;
	}
	public List<String> getTargetWords() {
		return targetWords;
	}
	public void setTargetWords(List<String> targetWords) {
		this.targetWords = targetWords;
	}
	public int[][] getAlignmentMatrix() {
		return alignmentMatrix;
	}
	public void setAlignmentMatrix(int[][] matrix) {
		this.alignmentMatrix = matrix;
	}
	
}
