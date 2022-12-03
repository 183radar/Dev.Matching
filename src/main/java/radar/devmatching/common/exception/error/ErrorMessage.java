package radar.devmatching.common.exception.error;

import org.springframework.http.HttpStatus;

public enum ErrorMessage {

	//common
	INVALID_ACCSS("Invalid Access", HttpStatus.BAD_REQUEST),

	//User
	DUPLICATE_USERNAME("duplicate username", HttpStatus.CONFLICT),
	DUPLICATE_NICKNAME("duplicate nickName", HttpStatus.CONFLICT),
	//권한이 없는 접근
	YOU_ARE_NOT_LEADER("오직 방장만 접근할 수 있습니다.", HttpStatus.BAD_REQUEST),

	//EntityNotFoundException
	SIMPLE_POST_NOT_FOUND("해당 id값을 갖는 엔티티가 존재하지 않습니다.", HttpStatus.NOT_FOUND);

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
