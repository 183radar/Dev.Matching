package radar.devmatching.common.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class InvalidParamException extends BusinessException {
	public InvalidParamException(ErrorMessage errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}
}
