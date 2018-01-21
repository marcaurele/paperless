package space.paperless.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;

import space.paperless.domain.DescriptionType;
import space.paperless.domain.Document;
import space.paperless.domain.RepositoryId;
import space.paperless.pdfmeta.PDFMetadata;
import space.paperless.repository.RepositoryIndex.DocumentIndexer;

public class DocumentsRepository {

	private RepositoryId repositoryId;
	private File root;
	private PDFMetadata pdfMetadata;
	private RepositoryIndex repositoryIndex;

	public DocumentsRepository(RepositoryId repositoryId, File root, PDFMetadata pdfMetadata,
			RepositoryIndex repositoryIndex) {
		this.repositoryId = repositoryId;
		this.root = root;
		this.pdfMetadata = pdfMetadata;
		this.repositoryIndex = repositoryIndex;
	}

	public File createFolder(String path) {
		File folder = getFolder(path);

		if (!folder.exists()) {
			folder.mkdirs();
		}

		return folder;
	}

	public List<String> getFoldersList(String path) {
		return Arrays.stream(getFolder(path).listFiles(File::isDirectory)).map(File::getName)
				.collect(Collectors.toList());
	}

	public Document getDocument(String documentId) throws IOException {
		File documentFile = getDocumentFile(documentId);

		if (!documentFile.exists()) {
			return null;
		}

		return new Document(documentId, pdfMetadata.readDescription(documentFile));
	}

	public InputStream getDocumentStream(String documentId) throws FileNotFoundException {
		File documentFile = getDocumentFile(documentId);

		return new FileInputStream(documentFile);
	}

	public List<Document> getDocuments(MultiValueMap<String, String> filters) throws IOException {
		List<Document> documents = null;

		if (isEmpty(filters)) {
			documents = getAllDocumentsList(new LinkedList<>(), "");
		} else {
			documents = repositoryIndex.search(filters);
		}

		Collections.sort(documents);

		return documents;
	}

	private boolean isEmpty(MultiValueMap<String, String> filters) {
		if (filters == null || filters.isEmpty()) {
			return true;
		}

		for (List<String> filter : filters.values()) {
			for (String value : filter) {
				if (!StringUtils.isBlank(value)) {
					return false;
				}
			}
		}

		return true;
	}

	public Document update(Document sourceDocument, DocumentsRepository sourceRepository) throws IOException {
		File sourceFile = sourceRepository.getDocumentFile(sourceDocument.getDocumentId());

		if (!sourceFile.exists()) {
			return null;
		}

		pdfMetadata.writeDescription(sourceFile, sourceDocument.toDescription());

		String destinationId = getDocumentId(sourceDocument);
		File destinationFile = getDocumentFile(destinationId);
		Document document = sourceDocument;

		if (!sourceFile.equals(destinationFile)) {
			if (destinationFile.exists()) {
				throw new FileExistsException(destinationFile);
			}

			Files.move(sourceFile.toPath(), destinationFile.toPath());
			document = new Document(destinationId, sourceDocument.getDescriptions());
		}

		repositoryIndex.updateIndex(sourceDocument.getDocumentId(), destinationFile, document);

		return document;
	}

	public List<Document> reindex() throws IOException {
		String[] descriptions = pdfMetadata.readDescriptions(root);

		if (descriptions != null && descriptions.length > 0) {
			List<Document> documents = new ArrayList<>(descriptions.length);
			DocumentIndexer indexer = repositoryIndex.createIndexer();

			try {
				indexer.open();
				indexer.deleteAll();

				for (String description : descriptions) {
					String[] parts = description.split("::");

					if (parts.length >= 2) {
						Path documentPath = Paths.get(parts[0], parts[1]);
						Document document = new Document(getDocumentId(documentPath), parts.length > 2 ? parts[2] : "");

						indexer.indexDocument(documentPath.toFile(), document);
						documents.add(document);
					}
				}

				return documents;
			} finally {
				indexer.close();
			}
		}

		return Collections.emptyList();
	}

	private List<Document> getAllDocumentsList(List<Document> documents, String path) {
		File folder = getFolder(path);
		File[] listFiles = folder.listFiles();

		if (listFiles != null) {
			for (File file : listFiles) {
				if (file.isDirectory()) {
					getAllDocumentsList(documents, IdUtils.path(path, file.getName()));
				} else {
					documents.add(new Document(getDocumentId(file.toPath())));
				}
			}
		}

		return documents;
	}

	private File getFolder(String path) {
		return !StringUtils.isBlank(path) ? new File(root, path) : root;
	}

	private String getDocumentId(Path filePath) {
		return IdUtils.id(repositoryId.getName(), root.toPath().toAbsolutePath().relativize(filePath).toString());
	}

	private String getDocumentId(Document document) {
		StringBuilder fullName = new StringBuilder();

		document.fixMandatoryDescriptionFields();
		fullName.append(document.getFirstDescriptionValue(DescriptionType.YEAR));
		fullName.append(StringUtils.leftPad(document.getFirstDescriptionValue(DescriptionType.MONTH), 2, '0'))
				.append('_');
		fullName.append(document.getFirstDescriptionValue(DescriptionType.NAME));

		if (RepositoryId.INCOMING.equals(repositoryId)) {
			return IdUtils.id(repositoryId.getName(), fullName.toString());
		} else {
			return IdUtils.id(repositoryId.getName(), document.getFirstDescriptionValue(DescriptionType.TYPE),
					fullName.toString());
		}
	}

	private File getDocumentFile(String documentId) {
		String path = IdUtils.idToPath(repositoryId.getName(), documentId);

		return new File(root, path);
	}

	public RepositoryId getRepositoryId() {
		return repositoryId;
	}

	public File getFilesRoot() {
		return root;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((repositoryId == null) ? 0 : repositoryId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentsRepository other = (DocumentsRepository) obj;
		return repositoryId == other.repositoryId;
	}
}
