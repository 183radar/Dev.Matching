package radar.devmatching.common.security.jwt.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class ExpiredRefreshTokenException extends TokenException {
	public ExpiredRefreshTokenException() {
		super(ErrorMessage.EXPIRED_REFRESH_TOKEN);
	}
}
