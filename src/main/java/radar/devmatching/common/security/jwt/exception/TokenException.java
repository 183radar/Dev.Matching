package radar.devmatching.common.security.jwt.exception;

import radar.devmatching.common.exception.BusinessException;
import radar.devmatching.common.exception.error.ErrorMessage;

public class TokenException extends BusinessException {
	public TokenException(ErrorMessage errorMessage) {
		super(errorMessage);
	}

	public TokenException(ErrorMessage errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}
}
