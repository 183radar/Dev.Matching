package radar.devmatching.common.exception.error;

import org.springframework.http.HttpStatus;

public enum ErrorMessage {

	//common

	//User
	DUPLICATE_USERNAME("duplicate username", HttpStatus.CONFLICT),
	DUPLICATE_NICKNAME("duplicate nickName", HttpStatus.CONFLICT),

	//MatchingUser
	ALREADY_JOIN("already join user", HttpStatus.BAD_REQUEST),

	//Apply
	APPLY_NOT_FOUND("not exist apply", HttpStatus.NOT_FOUND),
	ALREADY_APPLY("already apply", HttpStatus.BAD_REQUEST),

	//SimplePost
	SIMPLE_POST_NOT_FOUND("해당 id값을 갖는 엔티티가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

	//Invalid Access
	INVALID_ACCESS("Invalid Access", HttpStatus.BAD_REQUEST),
	NOT_LEADER("Invalid Access : Access User Not Leader", HttpStatus.BAD_REQUEST);

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
