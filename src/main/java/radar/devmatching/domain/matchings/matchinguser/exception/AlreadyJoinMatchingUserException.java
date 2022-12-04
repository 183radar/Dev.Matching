package radar.devmatching.domain.matchings.matchinguser.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class AlreadyJoinMatchingUserException extends MatchingUserException {

	public AlreadyJoinMatchingUserException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
