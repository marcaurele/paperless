package space.paperless.exif;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import space.paperless.command.Command;
import space.paperless.command.CommandFailedException;
import space.paperless.command.CommandResult;

@Component
public class Exif {

	private static final String EXIF_DESCRIPTION = "XMP:Keywords";
	private static final String OPTION_EXIF_DESCRIPTION = "-" + EXIF_DESCRIPTION;
	private static final String EXIF_NOTES = "Notes";
	private static final String OPTION_EXIF_NOTES = "-" + EXIF_NOTES;
	private static final String OPTION_EXIF_PRINT_FORMAT = "-p";
	private static final String EXIF_PRINT_FORMAT = "$directory::$filename::$" + EXIF_DESCRIPTION + "::$" + EXIF_NOTES;
	private static final String OPTION_EXIF_IGNORE_MINOR = "-ignoreMinorErrors";
	private static final String OPTION_EXIF_RECURSE = "-r";

	private final File exifTool;

	@Autowired
	public Exif(@Qualifier("exifTool") File exifTool) {
		this.exifTool = exifTool;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see space.paperless.exif.Exif#readDescription(java.io.File)
	 */
	public String readDescription(File fromFile) throws IOException {
		Command exif = new Command(exifTool);

		exif.add(OPTION_EXIF_DESCRIPTION);
		exif.add(OPTION_EXIF_NOTES);
		exif.add(OPTION_EXIF_IGNORE_MINOR);
		exif.add(OPTION_EXIF_PRINT_FORMAT).add(EXIF_PRINT_FORMAT);
		exif.add(fromFile.getAbsolutePath());

		CommandResult result = exif.execute();

		if (result.isSuccess()) {
			return result.getOutput().trim();
		} else {
			throw new CommandFailedException(result.getOutput());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see space.paperless.exif.Exif#readDescriptions(java.io.File)
	 */
	public String[] readDescriptions(File root) throws IOException {
		Command exif = new Command(exifTool);

		exif.add(OPTION_EXIF_DESCRIPTION);
		exif.add(OPTION_EXIF_NOTES);
		exif.add(OPTION_EXIF_IGNORE_MINOR);
		exif.add(OPTION_EXIF_PRINT_FORMAT).add(EXIF_PRINT_FORMAT);
		exif.add(OPTION_EXIF_RECURSE).add(root.getAbsolutePath());

		CommandResult result = exif.execute();

		if (result.isSuccess()) {
			return result.getOutput().trim().split("[\\r\\n]+");
		} else {
			throw new CommandFailedException(result.getOutput());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see space.paperless.exif.Exif#writeDescription(java.io.File,
	 * java.lang.String)
	 */
	public void writeDescription(File toFile, String description) throws IOException, CommandFailedException {
		Command exif = new Command(exifTool);

		if (!StringUtils.isBlank(description)) {
			exif.add(OPTION_EXIF_DESCRIPTION + "=" + description);
		}

		exif.add("-overwrite_original").add(toFile.getAbsolutePath());

		CommandResult result = exif.execute();

		if (!result.isSuccess()) {
			throw new CommandFailedException(result.getOutput());
		}

	}
}
