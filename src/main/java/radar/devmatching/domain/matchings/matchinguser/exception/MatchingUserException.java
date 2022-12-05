package radar.devmatching.domain.matchings.matchinguser.exception;

import radar.devmatching.common.exception.BusinessException;
import radar.devmatching.common.exception.error.ErrorMessage;

public class MatchingUserException extends BusinessException {

	public MatchingUserException(ErrorMessage errorMessage) {
		super(errorMessage);
	}

}
