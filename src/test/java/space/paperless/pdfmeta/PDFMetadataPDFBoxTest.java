package space.paperless.pdfmeta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class PDFMetadataPDFBoxTest {

	private PDFMetadataPDFBox pdfMetadata = new PDFMetadataPDFBox();

	@Test
	public void readDescriptions_archive_descriptionsAreRead() throws IOException {
		// given an existing file
		File document = new File("src/test/resources/archive");

		// when reading description
		String[] descriptions = pdfMetadata.readDescriptions(document);

		// then description is correct
		assertTrue(descriptions[1].matches(
				".*::201501_Captain_Future.pdf::complement=\\{complement\\},notes=\\{notes\\},thirdparty=\\{thirdParty\\},reference=\\{reference1\\},reference=\\{\\d+\\}::"));
	}

	@Test
	public void readDescription_existingFile_descriptionIsRead() throws IOException {
		// given an existing file
		File document = new File("src/test/resources/archive/type2/201410_Astro_Boy.pdf");

		// when reading description
		String description = pdfMetadata.readDescription(document);

		// then description is correct
		assertEquals(document.getParentFile().getAbsolutePath().replace('\\', '/') + "::" + document.getName()
				+ "::type={type2},complement={complement},notes={notes},thirdparty={thirdparty},reference={reference1},reference={reference2},year={2015},month={01},name={Astro_Boy.pdf}::",
				description);
	}

	@Test(expected = FileNotFoundException.class)
	public void readDescription_unknownFile_throwsFileNotFoundException() throws IOException {
		// given an invalid file
		File document = new File("thisPathDoesNotPointToAnyValidFile");

		// when reading description
		pdfMetadata.readDescription(document);

		// then CommandFailedException is thrown
	}

	@Test
	public void writeDescription_existingFile_descriptionIsWritten() throws IOException {
		// given an existing file
		File document = new File("src/test/resources/archive/type2/201501_Captain_Future.pdf");
		String description = "complement={complement},notes={notes},thirdparty={thirdParty},reference={reference1},reference={"
				+ System.currentTimeMillis() + "}";

		// when writing description
		pdfMetadata.writeDescription(document, description);

		// then description is correctly written
		String readDescription = pdfMetadata.readDescription(document);
		assertEquals(document.getParentFile().getAbsolutePath().replace('\\', '/') + "::" + document.getName() + "::"
				+ description + "::", readDescription);
	}

	@Test(expected = FileNotFoundException.class)
	public void writeDescription_unknownFile_throwsFileNotFoundException() throws IOException {
		// given an invalid file
		File document = new File("thisPathDoesNotPointToAnyValidFile");

		// when writing description
		pdfMetadata.writeDescription(document, "");

		// then CommandFailedException is thrown
	}

}
