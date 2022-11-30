package radar.devmatching.domain.user.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class DuplicateException extends UserException {

	public DuplicateException(String message, ErrorMessage errorMessage) {
		super(message, errorMessage);
	}

	public DuplicateException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
