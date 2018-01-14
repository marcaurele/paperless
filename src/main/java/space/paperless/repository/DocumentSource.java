package space.paperless.repository;

import java.io.File;

import space.paperless.domain.Document;

public class DocumentSource {
	private File documentFile;
	private Document document;

	public DocumentSource(File documentFile, Document document) {
		super();
		this.documentFile = documentFile;
		this.document = document;
	}

	public File getDocumentFile() {
		return documentFile;
	}

	public Document getDocument() {
		return document;
	}
}
