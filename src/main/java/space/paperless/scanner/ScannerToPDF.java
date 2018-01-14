package space.paperless.scanner;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import space.paperless.command.Command;
import space.paperless.command.CommandFailedException;
import space.paperless.command.CommandResult;
import space.paperless.domain.ScanOptions;
import space.paperless.domain.ScanResult;

@Component
public class ScannerToPDF {

	private static final String OPTION_SCAN_NUMBER_OF_SCANS = "-n";
	private static final String OPTION_SCAN_OUTPUT_DEST = "-o";
	private static final String OPTION_SCAN_SOURCE = "-p";

	@Autowired
	@Qualifier("scannerTool")
	private File scanner;

	public ScanResult scan(ScanOptions scanOptions, File toFolder) throws IOException, CommandFailedException {
		String toFile = getScanResultFile(toFolder);
		Command scan = new Command(scanner);

		scan.add(OPTION_SCAN_SOURCE).add(scanOptions.getSource().name());
		scan.add(OPTION_SCAN_OUTPUT_DEST).add(toFile);
		scan.add(OPTION_SCAN_NUMBER_OF_SCANS).add(scanOptions.getNumber());

		CommandResult commandResult = scan.execute();

		if (commandResult.isSuccess()) {
			return new ScanResult(toFile, commandResult);
		} else {
			throw new CommandFailedException(commandResult.getOutput());
		}
	}

	private String getScanResultFile(File root) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		return new File(root, dateFormat.format(new Date()) + "_document.pdf").getAbsolutePath();
	}
}
