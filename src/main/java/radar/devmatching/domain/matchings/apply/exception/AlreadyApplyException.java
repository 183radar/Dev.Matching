package radar.devmatching.domain.matchings.apply.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class AlreadyApplyException extends ApplyException {
	public AlreadyApplyException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
