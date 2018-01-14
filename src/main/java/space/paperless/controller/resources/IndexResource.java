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
}
