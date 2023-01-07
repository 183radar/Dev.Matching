package radar.devmatching.common.security.jwt;

import lombok.Builder;
import radar.devmatching.common.util.ExcludeJacocoGenerated;

public class JwtTokenCookieInfo {
	private final String header;
	private final String token;
	private final long expireTime;

	@Builder
	public JwtTokenCookieInfo(String header, String token, long expireTime) {
		this.header = header;
		this.token = token;
		this.expireTime = expireTime;
	}

	@Override
	@ExcludeJacocoGenerated
	public String toString() {
		return "JwtTokenCookieInfo{" +
			"header='" + header + '\'' +
			", token='" + token + '\'' +
			", expireTime=" + expireTime +
			'}';
	}

	public String getHeader() {
		return header;
	}

	public String getToken() {
		return token;
	}

	public long getExpireTime() {
		return expireTime;
	}
}
