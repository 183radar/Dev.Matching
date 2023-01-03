package radar.devmatching.common.security.jwt;

import lombok.Builder;

public class JwtTokenInfo {

	private final Long userId;
	private final String username;

	@Builder
	public JwtTokenInfo(Long userId, String username) {
		this.userId = userId;
		this.username = username;
	}

	@Override
	public String toString() {
		return "JwtTokenInfo{" +
			"userId=" + userId +
			", username='" + username + '\'' +
			'}';
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}
}
