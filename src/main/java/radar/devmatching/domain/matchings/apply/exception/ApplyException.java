package radar.devmatching.domain.matchings.apply.exception;

import radar.devmatching.common.exception.BusinessException;
import radar.devmatching.common.exception.error.ErrorMessage;

public class ApplyException extends BusinessException {
	public ApplyException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
