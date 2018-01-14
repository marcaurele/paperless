package space.paperless.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import space.paperless.domain.Document;
import space.paperless.repository.DocumentsRepository;

@RestController
@RequestMapping(value = { "/repositories/{repositoryId}" })
@ExposesResourceFor(Document.class)
public class FilesController {

	private final Map<String, DocumentsRepository> rootToRepository = new HashMap<>();

	@Autowired
	public FilesController(List<DocumentsRepository> filesRepositories) {
		super();

		for (DocumentsRepository filesRepository : filesRepositories) {
			rootToRepository.put(filesRepository.getRepositoryId().getName(), filesRepository);
		}
	}

	@RequestMapping(value = "/files/{documentId:.+}", produces = { "application/pdf" })
	public ResponseEntity<InputStreamResource> getDocument(@PathVariable String repositoryId,
			@PathVariable String documentId) throws IOException {
		DocumentsRepository repository = rootToRepository.get(repositoryId);

		if (repository == null) {
			return ResponseEntity.notFound().build();
		}

		InputStream inputStream = repository.getDocumentStream(documentId);

		return new ResponseEntity<InputStreamResource>(new InputStreamResource(inputStream), HttpStatus.OK);
	}
}
