package radar.devmatching.common.security.jwt.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class InvalidTokenException extends TokenException {
	public InvalidTokenException(Throwable e) {
		super(ErrorMessage.INVALID_TOKEN, e);
	}
}
