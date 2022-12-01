package radar.devmatching.domain.post.exception;

import radar.devmatching.common.exception.BusinessException;
import radar.devmatching.common.exception.error.ErrorMessage;

public class NotLeaderException extends BusinessException {
	public NotLeaderException() {
		super(ErrorMessage.YOU_ARE_NOT_LEADER);
	}
}
