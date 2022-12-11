package radar.devmatching.common.security.jwt.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class ExpiredAccessTokenException extends TokenException {
	public ExpiredAccessTokenException() {
		super(ErrorMessage.EXPIRED_ACCESS_TOKEN);
	}
}
