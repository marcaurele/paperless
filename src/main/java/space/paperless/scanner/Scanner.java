package space.paperless.scanner;

import java.io.File;
import java.io.IOException;

import space.paperless.domain.ScanOptions;
import space.paperless.domain.ScanResult;

public interface Scanner {

	ScanResult scan(ScanOptions scanOptions, File toFolder) throws IOException;

	String[] getSources();
}