package radar.devmatching.common.security.jwt.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class JwtTokenNotFoundException extends TokenException {
	public JwtTokenNotFoundException(ErrorMessage errorMessage) {
		super(errorMessage);
	}

	public JwtTokenNotFoundException(ErrorMessage errorMessage,
		Throwable throwable) {
		super(errorMessage, throwable);
	}
}
