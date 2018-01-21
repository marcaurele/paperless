package space.paperless.scanner.naps2;

public class CommandResult {
    private int exitCode;
    private String output;

    public CommandResult(int exitCode, String output) {
        super();
        this.exitCode = exitCode;
        this.output = output;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getOutput() {
        return output;
    }

    public boolean isSuccess() {
        return exitCode == 0;
    }

    @Override
    public String toString() {
        return "CommandResult [exitCode=" + exitCode + ", output=" + output + "]";
    }
}