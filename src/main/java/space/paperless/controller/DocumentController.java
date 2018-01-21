package space.paperless.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import space.paperless.domain.Document;
import space.paperless.domain.RepositoryId;
import space.paperless.repository.DescriptionIndexer;
import space.paperless.repository.DocumentsRepository;

@RestController
@RequestMapping(value = { "/repositories/{repositoryId}" })
@ExposesResourceFor(Document.class)
public class DocumentController {

	private final Map<String, DocumentsRepository> rootToRepository = new HashMap<>();
	private DescriptionIndexer descriptionIndexer;

	@Autowired
	public DocumentController(List<DocumentsRepository> filesRepositories, DescriptionIndexer descriptionIndexer) {
		super();

		this.descriptionIndexer = descriptionIndexer;

		for (DocumentsRepository filesRepository : filesRepositories) {
			rootToRepository.put(filesRepository.getRepositoryId().getName(), filesRepository);
		}
	}

	@RequestMapping(value = "/documents")
	public ResponseEntity<List<Resource<Document>>> getDocuments(@PathVariable String repositoryId,
			@RequestParam(required = false) MultiValueMap<String, String> filters)
			throws IOException, DecoderException {
		DocumentsRepository repository = rootToRepository.get(repositoryId);

		if (repository == null) {
			return ResponseEntity.notFound().build();
		}

		List<Resource<Document>> resources = new LinkedList<>();

		for (Document document : repository.getDocuments(filters)) {
			resources.add(getDocumentResource(repositoryId, document));
		}

		if (resources.isEmpty()) {
			return ResponseEntity.noContent().build();
		} else {
			return new ResponseEntity<>(resources, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/documents/{documentId:.+}")
	public ResponseEntity<Resource<Document>> getDocument(@PathVariable String repositoryId,
			@PathVariable String documentId) throws IOException, DecoderException {
		DocumentsRepository repository = rootToRepository.get(repositoryId);

		if (repository == null) {
			return ResponseEntity.notFound().build();
		}

		Document document = repository.getDocument(documentId);

		if (document == null) {
			return ResponseEntity.notFound().build();
		}

		return new ResponseEntity<>(getDocumentResource(repositoryId, document), HttpStatus.OK);
	}

	private Resource<Document> getDocumentResource(String repositoryId, Document document)
			throws IOException, DecoderException {
		Resource<Document> resource = new Resource<>(document);

		resource.add(linkTo(methodOn(DocumentController.class).getDocument(repositoryId, document.getDocumentId()))
				.withSelfRel());
		resource.add(linkTo(methodOn(FilesController.class).getDocument(repositoryId, document.getDocumentId()))
				.withRel("file"));

		return resource;
	}

	@RequestMapping(value = "/documents/{documentId:.+}", method = RequestMethod.PUT)
	public ResponseEntity<Resource<Document>> updateDocument(@PathVariable String repositoryId,
			@PathVariable String documentId, @RequestBody Document document,
			@RequestParam(defaultValue = "false") boolean archive) throws IOException, DecoderException {
		DocumentsRepository sourceRepository = rootToRepository.get(repositoryId);
		DocumentsRepository destinationRepository = rootToRepository
				.get(archive ? RepositoryId.ARCHIVE.getName() : sourceRepository.getRepositoryId().getName());

		if (sourceRepository == null || destinationRepository == null) {
			return ResponseEntity.notFound().build();
		}

		descriptionIndexer.index(document);

		// move document
		Document movedDocument = destinationRepository.update(document, sourceRepository);

		// TODO: create exception handler
		if (movedDocument != null) {
			return new ResponseEntity<>(getDocumentResource(repositoryId, movedDocument), HttpStatus.OK);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(value = "/documents/reindex", method = RequestMethod.POST)
	public ResponseEntity<Object> requestReindex(@PathVariable String repositoryId) throws IOException {
		DocumentsRepository sourceRepository = rootToRepository.get(repositoryId);
		List<Document> documents = sourceRepository.reindex();

		descriptionIndexer.index(documents);

		return ResponseEntity.noContent().build();
	}
}
