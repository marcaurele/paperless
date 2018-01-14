package space.paperless.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.util.MultiValueMap;

import space.paperless.domain.DescriptionType;
import space.paperless.domain.Document;
import space.paperless.domain.RepositoryId;

public class RepositoryIndex {

	private static final int MAX_HITS = 100;
	private static final String DOCUMENT_ID = "documentId";
	private static final String CONTENTS = "contents";

	/** Indexed, tokenized, stored. */
	public static final FieldType TYPE_TOKENIZED = new FieldType();

	/** Indexed, tokenized, stored. */
	public static final FieldType TYPE_NOT_TOKENIZED = new FieldType();

	static {
		TYPE_TOKENIZED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		TYPE_TOKENIZED.setTokenized(true);
		TYPE_TOKENIZED.setStored(true);
		TYPE_TOKENIZED.freeze();

		TYPE_NOT_TOKENIZED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		TYPE_NOT_TOKENIZED.setTokenized(false);
		TYPE_NOT_TOKENIZED.setStored(true);
		TYPE_NOT_TOKENIZED.freeze();
	}
	private final File indexRoot;

	private StandardAnalyzer analyzer;
	private Directory index;
	private PDFTextStripper stripper;

	public RepositoryIndex(RepositoryId repositoryId, File indexRoot) {
		this.indexRoot = new File(indexRoot, repositoryId.getName());
	}

	public DocumentIndexer createIndexer() {
		return new DocumentIndexer();
	}

	public void updateIndex(String originalSourceId, File documentFile, Document newDocument) throws IOException {
		try (IndexWriter indexWriter = openWriter()) {
			TermQuery query = new TermQuery(new Term(DOCUMENT_ID, originalSourceId));

			indexWriter.deleteDocuments(query);
			indexWriter.addDocument(toLuceneDocument(documentFile, newDocument));
		}
	}

	public List<Document> search(MultiValueMap<String, String> filters) throws IOException {
		BooleanQuery.Builder builder = new BooleanQuery.Builder();

		for (Map.Entry<String, List<String>> filter : filters.entrySet()) {
			if (filter.getValue() != null) {
				for (String value : filter.getValue()) {
					if (!StringUtils.isBlank(value)) {
						if (isTokenized(filter.getKey())) {
							builder.add(new WildcardQuery(new Term(filter.getKey(), "*" + value.toLowerCase() + "*")),
									BooleanClause.Occur.MUST);
						} else {
							builder.add(new TermQuery(new Term(filter.getKey(), value)), BooleanClause.Occur.MUST);
						}
					}
				}
			}
		}

		try (DirectoryReader directoryReader = openReader()) {
			IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
			TopDocs results = indexSearcher.search(builder.build(), MAX_HITS);
			ScoreDoc[] scoreDocs = results.scoreDocs;
			List<Document> documents = new ArrayList<>((int) results.totalHits);

			for (ScoreDoc scoreDoc : scoreDocs) {
				org.apache.lucene.document.Document doc = indexSearcher.doc(scoreDoc.doc);

				documents.add(new Document(doc.get(DOCUMENT_ID)));
			}

			return documents;
		}
	}

	private DirectoryReader openReader() throws IOException {
		if (index == null) {
			analyzer = new StandardAnalyzer();
			index = FSDirectory.open(Paths.get(indexRoot.toURI()));
		}

		return DirectoryReader.open(index);
	}

	private IndexWriter openWriter() throws IOException {
		if (index == null) {
			analyzer = new StandardAnalyzer();
			index = FSDirectory.open(Paths.get(indexRoot.toURI()));
		}

		return new IndexWriter(index, new IndexWriterConfig(analyzer));
	}

	private org.apache.lucene.document.Document toLuceneDocument(File documentFile, Document document)
			throws IOException {
		org.apache.lucene.document.Document luceneDocument = new org.apache.lucene.document.Document();

		// index id
		luceneDocument.add(new TextField(DOCUMENT_ID, document.getDocumentId(), Field.Store.YES));

		// index description
		for (Entry<String, Set<String>> description : document.getDescriptions().entrySet()) {
			if (description.getValue() != null && !description.getValue().isEmpty()) {
				for (String value : description.getValue()) {
					luceneDocument.add(new Field(description.getKey(), value,
							isTokenized(description.getKey()) ? TYPE_TOKENIZED : TYPE_NOT_TOKENIZED));
				}
			}
		}

		// index content
		if (documentFile != null) {
			StringWriter writer = new StringWriter();
			PDDocument pdfDocument = null;
			FileInputStream fileInputStream = null;

			try {
				fileInputStream = new FileInputStream(documentFile);
				pdfDocument = PDDocument.load(fileInputStream, "");

				if (stripper == null) {
					stripper = new PDFTextStripper();
				}

				stripper.writeText(pdfDocument, writer);
				luceneDocument.add(new TextField(CONTENTS, new StringReader(writer.getBuffer().toString())));
			} catch (InvalidPasswordException e) {
				throw new IOException("Error: The document(" + documentFile + ") is encrypted and will not be indexed.",
						e);
			} finally {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
				if (pdfDocument != null) {
					pdfDocument.close();
				}
			}
		}

		return luceneDocument;
	}

	public boolean isTokenized(String descriptionType) {
		return DescriptionType.NOTES.getName().equals(descriptionType) || CONTENTS.equals(descriptionType);
	}

	public class DocumentIndexer {
		private IndexWriter writer;

		private DocumentIndexer() {
			super();
		}

		public void open() throws IOException {
			writer = openWriter();
		}

		public void deleteAll() throws IOException {
			writer.deleteAll();
		}

		public void indexDocument(File documentFile, Document document) throws IOException {
			System.out.println("Indexing " + documentFile);
			writer.addDocument(toLuceneDocument(documentFile, document));
		}

		public void close() {
			if (writer == null) {
				return;
			}

			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
