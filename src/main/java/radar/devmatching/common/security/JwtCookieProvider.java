package radar.devmatching.common.security;

import java.util.Arrays;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

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

	public String getCookieFromRequest(HttpServletRequest request, String cookieName) {
		return Arrays.stream(request.getCookies())
			.filter(cookie -> Objects.equals(cookieName, cookie.getName()))
			.findFirst().get().getValue();
	}
}
