package space.paperless.domain;

import java.util.Set;
import java.util.SortedSet;

public abstract class DescriptionIndex {

	private DescriptionType descriptionType;

	public DescriptionIndex() {
		super();
	}

	public DescriptionIndex(DescriptionType descriptionType) {
		super();
		this.descriptionType = descriptionType;
	}

	public DescriptionType getDescriptionType() {
		return descriptionType;
	}

	public abstract SortedSet<String> getElements();

	public abstract void add(Set<String> ids);

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		DescriptionIndex that = (DescriptionIndex) o;

		return descriptionType == that.descriptionType;
	}

	@Override
	public int hashCode() {
		return descriptionType != null ? descriptionType.hashCode() : 0;
	}
}