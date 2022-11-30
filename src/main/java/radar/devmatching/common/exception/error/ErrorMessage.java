package radar.devmatching.common.exception.error;

import org.springframework.http.HttpStatus;

public enum ErrorMessage {

	//common
	INVALID_ACCSS("Invalid Access", HttpStatus.BAD_REQUEST),

	//User
	DUPLICATE_USERNAME("duplicate username", HttpStatus.CONFLICT),
	DUPLICATE_NICKNAME("duplicate nickName", HttpStatus.CONFLICT);

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
