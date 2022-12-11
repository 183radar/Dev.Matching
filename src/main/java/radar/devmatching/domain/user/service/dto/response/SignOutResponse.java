package radar.devmatching.domain.user.service.dto.response;

import lombok.Builder;

public class SignOutResponse {

	private final String accessTokenHeader;
	private final String refreshTokenHeader;

	@Builder
	public SignOutResponse(String accessTokenHeader, String refreshTokenHeader) {
		this.accessTokenHeader = accessTokenHeader;
		this.refreshTokenHeader = refreshTokenHeader;
	}

	public String getAccessTokenHeader() {
		return accessTokenHeader;
	}

	public String getRefreshTokenHeader() {
		return refreshTokenHeader;
	}
}
