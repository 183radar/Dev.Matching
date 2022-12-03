package radar.devmatching.common.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class CustomAuthenticationException extends BusinessException {
	public CustomAuthenticationException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
