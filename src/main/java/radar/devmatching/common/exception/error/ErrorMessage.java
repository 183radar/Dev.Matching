package radar.devmatching.common.exception.error;

import org.springframework.http.HttpStatus;

public enum ErrorMessage {
	;

	private final String message;
	private final HttpStatus status;

	ErrorMessage(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
