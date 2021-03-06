package space.paperless.scanner;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ScanningFailedException extends RuntimeException {

	private static final long serialVersionUID = 4258291963601894111L;

	public ScanningFailedException() {
	}

	public ScanningFailedException(String message) {
		super(message);
	}

	public ScanningFailedException(Throwable cause) {
		super(cause);
	}

	public ScanningFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScanningFailedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
