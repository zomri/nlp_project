package ex1.common;

import java.util.List;
import java.util.Set;

import com.beust.jcommander.internal.Lists;

public class WordTuple {

	private List<String> list;
	private double p;

	public WordTuple(List<String> list) {
		this.list = list;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordTuple other = (WordTuple) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		return true;
	}

	public List<String> list() {
		return list;
	}

	@Override
	public String toString() {
		return "WordTuple " + list;
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public static WordTuple createByVocabulary(WordTuple wordTuple,
			Set<String> modelVocaulary) {
		List<String> newArrayList = Lists.newArrayList(wordTuple.list());
		for (int i = 0; i < newArrayList.size(); i++) {
			if (!modelVocaulary.contains(newArrayList.get(i))) {
				newArrayList.set(i, Constants.UNK);
			}
		}
		WordTuple $ = new WordTuple(newArrayList);
		return $;
	}

	public WordTuple subTuple(int j) {
		return new WordTuple(list.subList(list.size() - 1 - j, list.size() - 1));
	}

	public static void main(String[] args) {
		WordTuple wordTuple = new WordTuple(Lists.newArrayList("a", "b", "c",
				"d"));
		for (int i = 1; i < 4; i++) {
			System.out.println(wordTuple.subTuple(i));
		}
	}

	public WordTuple lastWordTuple() {
		return new WordTuple(list.subList(list.size() - 1, list.size()));
	}

	public WordTuple subTupleWithLast(int j) {
		return new WordTuple(list.subList(list.size() - 1 - j, list.size()));
	}

}
