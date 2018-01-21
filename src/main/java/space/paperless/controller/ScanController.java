package space.paperless.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import space.paperless.domain.ScanOptions;
import space.paperless.domain.ScanResult;
import space.paperless.scanner.Scanner;
import space.paperless.scanner.ScanningFailedException;

@RestController
public class ScanController {

	@Autowired
	@Qualifier("incomingRoot")
	private File destination;

	@Autowired
	private Scanner scanner;

	@RequestMapping(value = "/scans", method = RequestMethod.POST)
	public ResponseEntity<ScanResult> scan(@RequestBody ScanOptions scanOptions)
			throws ScanningFailedException, IOException {
		ScanResult result;

		result = scanner.scan(scanOptions, destination);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping("/scannerSources")
	public ResponseEntity<String[]> sources() {
		return new ResponseEntity<String[]>(scanner.getSources(), HttpStatus.OK);
	}
}
