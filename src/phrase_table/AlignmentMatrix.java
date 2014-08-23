package phrase_table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

public class AlignmentMatrix {

	List<String> srcWords;
	List<String> targetWords;
	int[][] alignmentMatrix;
	
	Map<Integer, Set<Integer>> src2targetAlign;
	Map<Integer, Set<Integer>> target2srcAlign;
	
	
	public Map<Integer, Set<Integer>> getSrc2targetAlign() {
		return src2targetAlign;
	}
	public Map<Integer, Set<Integer>> getTarget2srcAlign() {
		return target2srcAlign;
	}
	
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
	public void setSrc2TargetAlignment(
			Map<Integer,Set<Integer>> src2targetAlign) {
		this.src2targetAlign = src2targetAlign;
		
	}
	public void setTarget2SrcAlignment(
			Map<Integer,Set<Integer>> target2srcAlign) {
		this.target2srcAlign = target2srcAlign;
		
	}
	
}
