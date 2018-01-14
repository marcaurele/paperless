package space.paperless.domain;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Calendar;

import org.junit.Test;

/**
 * Created by vince on 22.01.2017.
 */
public class DocumentTest {

	@Test
	public void create_archivePatternFileName_assignmentsAreCorrect() throws Exception {
		// given
		String documentId = "201610whatever_document1.pdf";
		String description = "";

		// when
		Document document = new Document(documentId, description);

		// then
		assertEquals(documentId, document.getDocumentId());
		assertEquals("2016", document.getFirstDescriptionValue(DescriptionType.YEAR));
		assertEquals("10", document.getFirstDescriptionValue(DescriptionType.MONTH));
		assertEquals("document1.pdf", document.getFirstDescriptionValue(DescriptionType.NAME));
	}

	@Test
	public void create_unknownPatternFileName_assignmentsAreCorrect() throws Exception {
		// given
		String documentId = "whatever.pdf";
		String description = "";

		// when
		Document document = new Document(documentId, description);

		// then
		Calendar today = Calendar.getInstance();

		assertEquals(documentId, document.getDocumentId());
		assertEquals(String.format("%1$tY", today), document.getFirstDescriptionValue(DescriptionType.YEAR));
		assertEquals(String.format("%1$tm", today), document.getFirstDescriptionValue(DescriptionType.MONTH));
		assertEquals("whatever.pdf", document.getFirstDescriptionValue(DescriptionType.NAME));
	}

	@Test
	public void create_descriptionIsParsedCorrectly() throws Exception {
		// given
		String documentId = "201610_document1.pdf";
		String description = "type={typeIsRead},complement={complementIsRead},notes={notesAreRead},thirdparty={thirdPartyIsRead},reference={reference1},reference={reference2}";

		// when
		Document document = new Document(documentId, description);

		// then
		assertEquals(8, document.getDescriptions().size());
		assertThat(document.getDescriptions().get(DescriptionType.TYPE.getName()), hasItem("typeIsRead"));
		assertThat(document.getDescriptions().get(DescriptionType.COMPLEMENT.getName()), hasItem("complementIsRead"));
		assertThat(document.getDescriptions().get(DescriptionType.NOTES.getName()), hasItem("notesAreRead"));
		assertThat(document.getDescriptions().get(DescriptionType.REFERENCE.getName()), hasItem("reference1"));
		assertThat(document.getDescriptions().get(DescriptionType.REFERENCE.getName()), hasItem("reference2"));
		assertThat(document.getDescriptions().get(DescriptionType.THIRDPARTY.getName()), hasItem("thirdPartyIsRead"));
		assertEquals("typeIsRead", document.getFirstDescriptionValue(DescriptionType.TYPE));
		assertEquals("complementIsRead", document.getFirstDescriptionValue(DescriptionType.COMPLEMENT));
		assertEquals("notesAreRead", document.getFirstDescriptionValue(DescriptionType.NOTES));
		assertEquals("reference1", document.getFirstDescriptionValue(DescriptionType.REFERENCE));
		assertEquals("thirdPartyIsRead", document.getFirstDescriptionValue(DescriptionType.THIRDPARTY));
		assertEquals("2016", document.getFirstDescriptionValue(DescriptionType.YEAR));
		assertEquals("10", document.getFirstDescriptionValue(DescriptionType.MONTH));
		assertEquals("document1.pdf", document.getFirstDescriptionValue(DescriptionType.NAME));
	}

	@Test
	public void create_oldDescriptionIsParsedCorrectly() throws Exception {
		// given
		String documentId = "201610_document1.pdf";
		String description = "blabla::blabla::thirdparty:Public/City/Youth;type:Attestation/Fee;reference:Person/One;reference:Person/Two::";

		// when
		Document document = new Document(documentId, description);

		// then
		assertEquals(6, document.getDescriptions().size());
		assertThat(document.getDescriptions().get(DescriptionType.TYPE.getName()), hasItem("Attestation/Fee"));
		assertThat(document.getDescriptions().get(DescriptionType.REFERENCE.getName()), hasItem("Person/One"));
		assertThat(document.getDescriptions().get(DescriptionType.REFERENCE.getName()), hasItem("Person/Two"));
		assertThat(document.getDescriptions().get(DescriptionType.THIRDPARTY.getName()), hasItem("Public/City/Youth"));
		assertEquals("Attestation/Fee", document.getFirstDescriptionValue(DescriptionType.TYPE));
		assertEquals("Person/One", document.getFirstDescriptionValue(DescriptionType.REFERENCE));
		assertEquals("Public/City/Youth", document.getFirstDescriptionValue(DescriptionType.THIRDPARTY));
		assertEquals("2016", document.getFirstDescriptionValue(DescriptionType.YEAR));
		assertEquals("10", document.getFirstDescriptionValue(DescriptionType.MONTH));
		assertEquals("document1.pdf", document.getFirstDescriptionValue(DescriptionType.NAME));
	}
}