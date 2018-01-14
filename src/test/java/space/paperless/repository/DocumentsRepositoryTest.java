package space.paperless.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import space.paperless.command.CommandFailedException;
import space.paperless.domain.DescriptionType;
import space.paperless.domain.Document;
import space.paperless.domain.RepositoryId;

/**
 * Created by vince on 21.01.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@TestPropertySource("/test-application.properties")
public class DocumentsRepositoryTest {

	public static final String DOCUMENT1_PDF = "201410_Astro_Boy.pdf";
	public static final String DOCUMENT2_PDF = "201501_Captain_Future.pdf";
	private static final String DOCUMENT3_PDF = "201410_Astro_Boy_source.pdf";
	private static final String DOCUMENT4_PDF = "201410_Astro_Boy_destination.pdf";

	private static final String TYPE1_FOLDER = "type1";
	private static final String TYPE2_FOLDER = "type2";

	@Autowired()
	@Qualifier("archive")
	private DocumentsRepository filesRepository;

	@Test
	public void createFolder_existingFolder() {
		// when
		File path = filesRepository.createFolder(TYPE1_FOLDER);

		// then
		assertEquals(new File(filesRepository.getFilesRoot(), TYPE1_FOLDER), path);
		assertTrue(path.exists());
	}

	@Test
	public void getDocumentId_validDocument_returnsDocumentId() throws Exception {
		// when
		String id = IdUtils.id(RepositoryId.ARCHIVE.getName(), TYPE2_FOLDER, DOCUMENT1_PDF);
		Document document = filesRepository.getDocument(id);

		// then
		assertEquals(id, document.getDocumentId());
		assertEquals(TYPE2_FOLDER, document.getFirstDescriptionValue(DescriptionType.TYPE));
	}

	@Test
	public void reindex_archive_returnsAllDocuments() throws Exception {
		// when
		String id = IdUtils.id(RepositoryId.ARCHIVE.getName(), TYPE2_FOLDER, DOCUMENT1_PDF);
		List<Document> documents = filesRepository.reindex();

		// then
		assertEquals(id, documents.get(0).getDocumentId());
		assertEquals(TYPE2_FOLDER, documents.get(0).getFirstDescriptionValue(DescriptionType.TYPE));
	}

	@Test
	public void update_existingDocument_documentIsUpdated() throws CommandFailedException, IOException {
		// given
		String sourceDocumentId = createPDF(TYPE2_FOLDER, DOCUMENT3_PDF);
		String destinationDocumentId = IdUtils.id(RepositoryId.ARCHIVE.getName(), TYPE1_FOLDER, DOCUMENT4_PDF);
		String randomNotes = String.valueOf(RandomUtils.nextLong());
		Document sourceDocument = new Document(sourceDocumentId, "type={" + TYPE1_FOLDER
				+ "},complement={type3},notes={" + randomNotes
				+ "},thirdparty={third party},reference={reference1},reference={reference2},year={2014},month={10},name={Astro_Boy_destination.pdf}");

		// when
		Document returnedDocumentValue = filesRepository.update(sourceDocument, filesRepository);
		Document destinationDocument = filesRepository.getDocument(destinationDocumentId);

		// then returned document has correct values
		assertEquals(destinationDocumentId, returnedDocumentValue.getDocumentId());
		assertEquals(TYPE1_FOLDER, returnedDocumentValue.getFirstDescriptionValue(DescriptionType.TYPE));
		assertEquals("type3", returnedDocumentValue.getFirstDescriptionValue(DescriptionType.COMPLEMENT));
		assertEquals(randomNotes, returnedDocumentValue.getFirstDescriptionValue(DescriptionType.NOTES));
		assertEquals("third party", returnedDocumentValue.getFirstDescriptionValue(DescriptionType.THIRDPARTY));
		assertEquals("reference1", returnedDocumentValue.getFirstDescriptionValue(DescriptionType.REFERENCE));
		assertEquals("2014", returnedDocumentValue.getFirstDescriptionValue(DescriptionType.YEAR));
		assertEquals("10", returnedDocumentValue.getFirstDescriptionValue(DescriptionType.MONTH));
		assertEquals("Astro_Boy_destination.pdf", returnedDocumentValue.getFirstDescriptionValue(DescriptionType.NAME));

		// then correct values are persisted
		assertEquals(destinationDocumentId, destinationDocument.getDocumentId());
		assertEquals(TYPE1_FOLDER, destinationDocument.getFirstDescriptionValue(DescriptionType.TYPE));
		assertEquals("type3", destinationDocument.getFirstDescriptionValue(DescriptionType.COMPLEMENT));
		assertEquals(randomNotes, destinationDocument.getFirstDescriptionValue(DescriptionType.NOTES));
		assertEquals("third party", destinationDocument.getFirstDescriptionValue(DescriptionType.THIRDPARTY));
		assertEquals("reference1", destinationDocument.getFirstDescriptionValue(DescriptionType.REFERENCE));
		assertEquals("2014", destinationDocument.getFirstDescriptionValue(DescriptionType.YEAR));
		assertEquals("10", destinationDocument.getFirstDescriptionValue(DescriptionType.MONTH));
		assertEquals("Astro_Boy_destination.pdf", destinationDocument.getFirstDescriptionValue(DescriptionType.NAME));
	}

	private String createPDF(String... parts) throws IOException {
		File template = new File(filesRepository.getFilesRoot(), IdUtils.path(TYPE2_FOLDER, DOCUMENT1_PDF));
		File pdfFile = new File(filesRepository.getFilesRoot(), IdUtils.path(parts));

		Files.copy(template.toPath(), pdfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		return IdUtils.id(RepositoryId.ARCHIVE.getName(), IdUtils.id(parts));
	}

	@After
	public void removeDocument4() {
		File document4 = new File(filesRepository.getFilesRoot(), IdUtils.path(TYPE1_FOLDER, DOCUMENT4_PDF));

		if (document4.exists()) {
			document4.delete();
		}
	}

}