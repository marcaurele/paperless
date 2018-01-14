package space.paperless.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DescriptionType {

	NAME("name", false, false, false), MONTH("month", true, false, false), YEAR("year", true, false, false), TYPE(
			"type", false, true, false), NOTES("notes", false, false, false), COMPLEMENT("complement", false, true,
					false), THIRDPARTY("thirdparty", false, true, true), REFERENCE("reference", false, true, true);

	private String name;
	private boolean number;
	private boolean indexed;
	private boolean collection;

	private DescriptionType(String name, boolean number, boolean indexed, boolean collection) {
		this.name = name;
		this.number = number;
		this.indexed = indexed;
		this.collection = collection;
	}

	public String getName() {
		return name;
	}

	public boolean isNumber() {
		return number;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public boolean isCollection() {
		return collection;
	}

	public static DescriptionType findByName(String name) {
		for (DescriptionType type : values()) {
			if (type.getName().equals(name)) {
				return type;
			}
		}

		throw new IllegalArgumentException("Unknow description: " + name);
	}
}