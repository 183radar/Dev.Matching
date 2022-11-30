package radar.devmatching.domain.user.exception;

import radar.devmatching.common.exception.BusinessException;
import radar.devmatching.common.exception.error.ErrorMessage;

public class UserException extends BusinessException {
	
	public UserException(String message, ErrorMessage errorMessage) {
		super(message, errorMessage);
	}

	public UserException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
