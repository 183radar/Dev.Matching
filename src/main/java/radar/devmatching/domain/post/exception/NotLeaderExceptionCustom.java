package radar.devmatching.domain.post.exception;

import radar.devmatching.common.exception.CustomAuthenticationException;
import radar.devmatching.common.exception.error.ErrorMessage;

public class NotLeaderExceptionCustom extends CustomAuthenticationException {
	public NotLeaderExceptionCustom() {
		super(ErrorMessage.NOT_LEADER);
	}
}
