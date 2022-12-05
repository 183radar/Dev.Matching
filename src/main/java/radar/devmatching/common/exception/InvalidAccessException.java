package radar.devmatching.common.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class InvalidAccessException extends BusinessException {

	public InvalidAccessException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
