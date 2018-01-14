package space.paperless.controller.resources;

import org.springframework.hateoas.ResourceSupport;

public class RepositoryIdResource extends ResourceSupport {

	private String name;

	public RepositoryIdResource() {
		super();
	}

	public RepositoryIdResource(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
