package radar.devmatching.domain.user.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class EmptySpaceException extends UserException {
	public EmptySpaceException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
