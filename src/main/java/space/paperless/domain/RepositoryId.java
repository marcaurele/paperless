package space.paperless.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RepositoryId {
	ARCHIVE("archive"), INCOMING("incoming");

	private String name;

	private RepositoryId(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static RepositoryId findByName(String name) {
		for (RepositoryId type : values()) {
			if (type.getName().equals(name)) {
				return type;
			}
		}

		throw new IllegalArgumentException("Unknow repository: " + name);
	}
}
