package space.paperless.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Command {
	private static final Logger LOG = LoggerFactory.getLogger(Command.class);

	private ProcessBuilder processBuilder;

	public Command(File executable) {
		super();
		processBuilder = new ProcessBuilder(executable.toString());
	}

	public Command add(String element) {
		processBuilder.command().add(element);
		return this;
	}

	public Command add(int element) {
		processBuilder.command().add(String.valueOf(element));
		return this;
	}

	public CommandResult execute() throws IOException {
		LOG.info("About to execute {}", processBuilder.command().toString());

		// processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();
		StringBuilder output = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;

		while ((line = in.readLine()) != null) {
			output.append(line).append("\n");
		}

		return new CommandResult(process.exitValue(), output.toString());
	}

	@Override
	public String toString() {
		return "Command [processBuilder=" + processBuilder + "]";
	}
}