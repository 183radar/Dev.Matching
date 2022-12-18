package radar.devmatching.common.exception.error;

import org.springframework.http.HttpStatus;

public enum ErrorMessage {

	//common

	//User
	DUPLICATE_USERNAME("duplicate username", HttpStatus.CONFLICT),
	DUPLICATE_NICKNAME("duplicate nickName", HttpStatus.CONFLICT),
	USER_NOT_FOUND("not exist user", HttpStatus.BAD_REQUEST),

	//MatchingUser
	ALREADY_JOIN("already join user", HttpStatus.BAD_REQUEST),

	//Apply
	APPLY_NOT_FOUND("not exist apply", HttpStatus.NOT_FOUND),
	ALREADY_APPLY("already apply", HttpStatus.BAD_REQUEST),

	//SimplePost
	SIMPLE_POST_NOT_FOUND("not exist simplePost entity of this id", HttpStatus.NOT_FOUND),

	//MainComment
	MAIN_COMMENT_NOT_FOUND("not exist mainComment entity of this id", HttpStatus.NOT_FOUND),

	//SubComment
	SUB_COMMENT_NOT_FOUND("not exist subComment entity of this id", HttpStatus.NOT_FOUND),

	//Invalid Access
	INVALID_ACCESS("Invalid Access", HttpStatus.BAD_REQUEST),
	NOT_LEADER("Invalid Access : Access User Not Leader", HttpStatus.BAD_REQUEST),
	NOT_COMMENT_OWNER("Invalid Access : access user is not comment owner", HttpStatus.BAD_REQUEST),

	//Security
	AUTHENTICATION_FAIL("Authentication Fail", HttpStatus.BAD_REQUEST),
	AUTHORITY_NOT_FOUND("Authority not found", HttpStatus.NOT_FOUND),
	INVALID_TOKEN("Invalid Token", HttpStatus.BAD_REQUEST),
	ACCESS_TOKEN_NOT_FOUND("access token not found", HttpStatus.NOT_FOUND),
	REFRESH_TOKEN_NOT_FOUND("refresh token not found", HttpStatus.NOT_FOUND),
	EXPIRED_ACCESS_TOKEN("expired access token", HttpStatus.BAD_REQUEST),
	EXPIRED_REFRESH_TOKEN("expired refresh token", HttpStatus.BAD_REQUEST),
	TOKEN_NOT_FOUND("token not found", HttpStatus.NOT_FOUND),

	//Invalid Param
	INVALID_POST_CATEGORY("Invalid PostCategoryType Is Inputted", HttpStatus.BAD_REQUEST);

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
