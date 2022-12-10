package radar.devmatching.domain.user.service.dto.response;

import lombok.Builder;
import radar.devmatching.common.security.jwt.JwtToken;

public class SignInResponse {

	private final JwtToken accessToken;
	private final JwtToken refreshToken;

	@Builder
	public SignInResponse(JwtToken accessToken, JwtToken refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public JwtToken getAccessToken() {
		return accessToken;
	}

	public JwtToken getRefreshToken() {
		return refreshToken;
	}
}
