package space.paperless.controller.resources;

import java.util.SortedSet;

import org.springframework.hateoas.ResourceSupport;

import space.paperless.domain.DescriptionIndex;
import space.paperless.domain.DescriptionType;

public class IndexResource extends ResourceSupport {

	private DescriptionType descriptionType;
	private SortedSet<String> elements;

	public IndexResource() {
		super();
	}

	public IndexResource(DescriptionIndex index) {
		super();
		this.descriptionType = index.getDescriptionType();
		this.elements = index.getElements();
	}

	public IndexResource(DescriptionType descriptionType, SortedSet<String> elements) {
		super();
		this.descriptionType = descriptionType;
		this.elements = elements;
	}

	public DescriptionType getDescriptionType() {
		return descriptionType;
	}

	public SortedSet<String> getElements() {
		return elements;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((descriptionType == null) ? 0 : descriptionType.hashCode());
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexResource other = (IndexResource) obj;
		if (descriptionType != other.descriptionType)
			return false;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}
}
