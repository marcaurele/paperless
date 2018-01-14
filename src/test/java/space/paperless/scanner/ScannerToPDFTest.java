package space.paperless.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import space.paperless.command.CommandFailedException;
import space.paperless.domain.ScanOptions;
import space.paperless.domain.ScanResult;
import space.paperless.domain.ScanSource;
import space.paperless.scanner.ScannerToPDF;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@TestPropertySource("/test-application.properties")
@Ignore
public class ScannerToPDFTest {

	private static final File ROOT = new File(new File("").getAbsolutePath(), "");

	@Autowired
	private ScannerToPDF scanner;
	private File pdf;

	@Test
	public void scan_scans() throws CommandFailedException, IOException {
		// given
		ScanOptions scanOptions = new ScanOptions();

		scanOptions.setNumber(1);
		scanOptions.setSource(ScanSource.CLX3175FW_GLASS);

		// when
		ScanResult scan = scanner.scan(scanOptions, ROOT);
		pdf = new File(scan.getFileName());

		// then
		assertTrue(scan.isSuccess());
		assertEquals("", scan.getOutput());
		assertTrue(pdf.exists());
	}

	@After
	public void cleanUp() {
		if (pdf.exists()) {
			pdf.delete();
		}
	}
}
