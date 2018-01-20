package space.paperless.pdfmeta;

import java.io.File;
import java.io.IOException;

import space.paperless.command.CommandFailedException;

public interface PDFMetadata {

	/*
	 * (non-Javadoc)
	 * 
	 * @see space.paperless.exif.Exif#readDescription(java.io.File)
	 */
	String readDescription(File fromFile) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see space.paperless.exif.Exif#readDescriptions(java.io.File)
	 */
	String[] readDescriptions(File root) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see space.paperless.exif.Exif#writeDescription(java.io.File,
	 * java.lang.String)
	 */
	void writeDescription(File toFile, String description) throws IOException, CommandFailedException;

}