package radar.devmatching.domain.user.service.dto.response;

import lombok.Builder;
import radar.devmatching.common.security.jwt.JwtTokenCookieInfo;

public class SignInResponse {

	private final JwtTokenCookieInfo accessToken;
	private final JwtTokenCookieInfo refreshToken;

	@Builder
	public SignInResponse(JwtTokenCookieInfo accessToken, JwtTokenCookieInfo refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public JwtTokenCookieInfo getAccessToken() {
		return accessToken;
	}

	public JwtTokenCookieInfo getRefreshToken() {
		return refreshToken;
	}
}
