package space.paperless.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import space.paperless.controller.resources.RepositoryIdResource;
import space.paperless.domain.RepositoryId;

@RestController
@RequestMapping("/repositories")
@ExposesResourceFor(RepositoryIdResource.class)
public class RepositoryController {

	@RequestMapping()
	public List<RepositoryIdResource> getRepositories() throws IOException {
		List<RepositoryIdResource> repositories = new LinkedList<>();

		for (RepositoryId repositoryId : RepositoryId.values()) {
			repositories.add(getRepositoryIdResource(repositoryId));
		}

		return repositories;
	}

	@RequestMapping("/{repositoryIdParam}")
	public ResponseEntity<RepositoryIdResource> getRepository(@PathVariable String repositoryIdParam)
			throws IOException {
		RepositoryId repositoryId = RepositoryId.findByName(repositoryIdParam);

		if (repositoryId == null) {
			return ResponseEntity.notFound().build();
		}

		return new ResponseEntity<>(getRepositoryIdResource(repositoryId), HttpStatus.OK);
	}

	private RepositoryIdResource getRepositoryIdResource(RepositoryId repositoryId) throws IOException {
		RepositoryIdResource resource = new RepositoryIdResource(repositoryId.getName());

		resource.add(linkTo(methodOn(RepositoryController.class).getRepository(repositoryId.getName())).withSelfRel());
		resource.add(linkTo(DocumentController.class, repositoryId.getName()).slash("/documents").withRel("documents"));

		return resource;
	}
}
