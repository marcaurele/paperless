package space.paperless.scanner.naps2;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import space.paperless.domain.ScanOptions;
import space.paperless.domain.ScanResult;
import space.paperless.scanner.Scanner;
import space.paperless.scanner.ScanningFailedException;

@Component
public class ScannerNAPS2 implements Scanner {

	private static final String OPTION_SCAN_NUMBER_OF_SCANS = "-n";
	private static final String OPTION_SCAN_OUTPUT_DEST = "-o";
	private static final String OPTION_SCAN_SOURCE = "-p";

	@Value("${tool.naps2}")
	private String pathToScannerExecutable;

	@Value("#{'${tool.naps2.sources}'.split(',')}")
	private String[] sources;

	/*
	 * (non-Javadoc)
	 * 
	 * @see space.paperless.scanner.Scanner#scan(space.paperless.domain.ScanOptions,
	 * java.io.File)
	 */
	@Override
	public ScanResult scan(ScanOptions scanOptions, File toFolder) throws IOException {
		String toFile = getScanResultFile(toFolder);
		Command scan = new Command(pathToScannerExecutable);

		scan.add(OPTION_SCAN_SOURCE).add(scanOptions.getSource());
		scan.add(OPTION_SCAN_OUTPUT_DEST).add(toFile);
		scan.add(OPTION_SCAN_NUMBER_OF_SCANS).add(scanOptions.getNumber());

		CommandResult commandResult = scan.execute();

		if (commandResult.isSuccess()) {
			return new ScanResult(toFile, commandResult);
		} else {
			throw new ScanningFailedException(commandResult.getOutput());
		}
	}

	@Override
	public String[] getSources() {
		return sources;
	}

	private String getScanResultFile(File root) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		return new File(root, dateFormat.format(new Date()) + "_document.pdf").getAbsolutePath();
	}
}
