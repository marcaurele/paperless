package space.paperless.repository;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import space.paperless.domain.Document;
import space.paperless.domain.RepositoryId;

/**
 * Created by vince on 22.01.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/test-application.properties")
public class RepositoryIndexTest {

	@Autowired()
	@Qualifier("incomingIndex")
	private RepositoryIndex index;

	@Autowired
	@Qualifier("indexRoot")
	private File indexRoot;

	@Autowired
	@Qualifier("incomingRoot")
	private File incomingRoot;

	@Before
	public void setUp() throws Exception {
		if (!indexRoot.exists()) {
			indexRoot.mkdir();
		}
	}

	@After
	public void tearDown() throws Exception {
		if (indexRoot.exists()) {
			FileUtils.deleteDirectory(indexRoot);
		}
	}

	@Test
	public void test_indexDocument_findDocument() throws Exception {
		// given
		String documentId = IdUtils.id(RepositoryId.ARCHIVE.getName(), "nonEmpty/201610_document1.pdf");
		Document document = new Document(documentId,
				"type={nonEmpty},complement={myComplement},notes={myNotes},thirdparty={myThirdParty},reference={myReference1},reference={myReference2}");

		// when
		index.updateIndex(documentId, null, document);

		// then
		assertThat(index.search(toFilter("type", "nonEmpty")), hasItem(document));
		assertThat(index.search(toFilter("complement", "myComplement")), hasItem(document));
		assertThat(index.search(toFilter("notes", "myNotes")), hasItem(document));
		assertThat(index.search(toFilter("notes", "mynotes")), hasItem(document));
		assertThat(index.search(toFilter("notes", "my")), hasItem(document));
		assertThat(index.search(toFilter("thirdparty", "myThirdParty")), hasItem(document));
		assertThat(index.search(toFilter("reference", "myReference1")), hasItem(document));
		assertThat(index.search(toFilter("reference", "myReference2")), hasItem(document));
	}

	@Test
	public void test_reIndexDocument_replacesDocument() throws Exception {
		// given
		String sourceDocumentId = IdUtils.id(RepositoryId.ARCHIVE.getName(), "source/201610_document1.pdf");
		String destinationDocumentId = IdUtils.id(RepositoryId.ARCHIVE.getName(), "destination/201610_document1.pdf");
		Document sourceDocument = new Document(sourceDocumentId, "type={source},complement={myComplementSource}");
		Document destinationDocument = new Document(destinationDocumentId,
				"type={destination},complement={myComplementDestination}");

		index.updateIndex(sourceDocumentId, null, sourceDocument);

		// when
		List<Document> beforeReindex = index.search(toFilter("type", "source"));
		index.updateIndex(sourceDocumentId, null, destinationDocument);
		List<Document> afterReindex = index.search(toFilter("type", "destination"));

		// then
		assertThat(beforeReindex, hasItem(sourceDocument));
		assertThat(afterReindex, hasItem(destinationDocument));
		assertThat(afterReindex, not(hasItem(sourceDocument)));
	}

	@Test
	public void test_indexDocument_contentIsIndexed() throws Exception {
		// given
		String documentId = IdUtils.id(RepositoryId.INCOMING.getName(), "20170713234803847_Battle_of_the_Planets.pdf");
		Document document = new Document(documentId,
				"type={nonEmpty},complement={myComplement},notes={myNotes},thirdparty={myThirdParty},reference={myReference1},reference={myReference2}");
		File documentFile = new File(incomingRoot, IdUtils.idToPath(RepositoryId.INCOMING.getName(), documentId));

		// when
		index.updateIndex(documentId, documentFile, document);

		// then
		assertThat(index.search(toFilter("contents", "*Gatchaman*")), hasItem(document));
	}

	private MultiValueMap<String, String> toFilter(String field, String value) {
		MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
		List<String> type = Arrays.asList(new String[] { value });

		filters.put(field, type);

		return filters;
	}
}