package radar.devmatching.common.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class BusinessException extends RuntimeException {

	private final ErrorMessage errorMessage;

	public BusinessException(String message, ErrorMessage errorMessage) {
		super(message);
		this.errorMessage = errorMessage;
	}

	public BusinessException(ErrorMessage errorMessage) {
		this.errorMessage = errorMessage;
	}

}
