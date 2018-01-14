package space.paperless.command;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CommandFailedException extends RuntimeException {

	private static final long serialVersionUID = 4258291963601894111L;

	public CommandFailedException() {
	}

	public CommandFailedException(String message) {
		super(message);
	}

	public CommandFailedException(Throwable cause) {
		super(cause);
	}

	public CommandFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandFailedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
