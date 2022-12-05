package radar.devmatching.domain.user.exception;

import radar.devmatching.common.exception.BusinessException;
import radar.devmatching.common.exception.error.ErrorMessage;

public class UserException extends BusinessException {

	public UserException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
