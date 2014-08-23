package phrase_table;

public class StringDoublePair {

	public StringDoublePair(Double d, String s) {
		this.d = d;
		this.s = s;
	}
	public Double getD() {
		return d;
	}
	public void setD(Double d) {
		this.d = d;
	}
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	private Double d;
	private String s;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((s == null) ? 0 : s.hashCode());
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
		StringDoublePair other = (StringDoublePair) obj;
		if (s == null) {
			if (other.s != null)
				return false;
		} else if (!s.equals(other.s))
			return false;
		return true;
	}
	
	
}
