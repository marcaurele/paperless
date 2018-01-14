package space.paperless.domain;

public class ScanOptions {

	private ScanSource source;
	private int number;

	public ScanSource getSource() {
		return source;
	}

	public void setSource(ScanSource source) {
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
