package radar.devmatching.domain.post.simple.exception;

import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.error.ErrorMessage;

public class SimplePostNotFoundException extends EntityNotFoundException {
	public SimplePostNotFoundException() {
		super(ErrorMessage.SIMPLE_POST_NOT_FOUND);
	}
}
