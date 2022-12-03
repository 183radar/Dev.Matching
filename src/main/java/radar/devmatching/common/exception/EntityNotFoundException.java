package radar.devmatching.common.exception;

import radar.devmatching.common.exception.error.ErrorMessage;

public class EntityNotFoundException extends BusinessException {
	public EntityNotFoundException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
