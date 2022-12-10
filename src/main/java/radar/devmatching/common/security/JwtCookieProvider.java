package radar.devmatching.common.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieProvider {

	public ResponseCookie createCookie(String header, String token, long expireTime) {
		return ResponseCookie.from(header, token)
			.path("/")
			.httpOnly(true)
			.maxAge(expireTime)
			.build();
	}
}
