package space.paperless.domain;

import space.paperless.command.CommandResult;

public class ScanResult {

	private String fileName;
	private CommandResult commandResult;

	public ScanResult(String fileName, CommandResult commandResult) {
		super();
		this.fileName = fileName;
		this.commandResult = commandResult;
	}

	public boolean isSuccess() {
		return commandResult.isSuccess();
	}

	public String getFileName() {
		return fileName;
	}

	public String getOutput() {
		return commandResult.getOutput();
	}

	@Override
	public String toString() {
		return "ScanResult [fileName=" + fileName + ", commandResult=" + commandResult + "]";
	}
}
