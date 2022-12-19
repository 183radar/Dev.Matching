package radar.devmatching.common.security;

import java.util.Arrays;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseCookie;

import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.common.security.jwt.exception.JwtTokenNotFoundException;

@Slf4j
public class JwtCookieProvider {

	public static ResponseCookie createCookie(String header, String token, long expireTime) {
		return ResponseCookie.from(header, token)
			.path("/")
			.httpOnly(true)
			.maxAge(expireTime)
			.build();
	}

	public static String getCookieFromRequest(HttpServletRequest request, String cookieName) {
		if (Objects.isNull(request.getCookies())) {
			throw new JwtTokenNotFoundException(ErrorMessage.TOKEN_NOT_FOUND);
		}

		return Arrays.stream(request.getCookies())
			.filter(cookie -> Objects.equals(cookieName, cookie.getName()))
			.findFirst()
			.orElseThrow(() -> new JwtTokenNotFoundException(ErrorMessage.TOKEN_NOT_FOUND))
			.getValue();
	}
}
