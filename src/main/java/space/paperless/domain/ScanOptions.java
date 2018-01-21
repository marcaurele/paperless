package space.paperless.domain;

public class ScanOptions {

	private String source;
	private int number;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "ScanOptions [source=" + source + ", number=" + number + "]";
	}
}
