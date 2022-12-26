package radar.devmatching.common.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class UsernamePasswordNotMatchException extends BusinessException {
	public UsernamePasswordNotMatchException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
