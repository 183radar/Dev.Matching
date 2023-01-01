package radar.devmatching.common.security;

import static org.springframework.http.HttpHeaders.*;

import java.util.Arrays;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

	public static String getCookieFromRequest(String cookieName) {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();

		if (Objects.isNull(request.getCookies())) {
			throw new JwtTokenNotFoundException(ErrorMessage.TOKEN_NOT_FOUND);
		}

		return Arrays.stream(request.getCookies())
			.filter(cookie -> Objects.equals(cookieName, cookie.getName()))
			.findFirst()
			.orElseThrow(() -> new JwtTokenNotFoundException(ErrorMessage.TOKEN_NOT_FOUND))
			.getValue();
	}

	public static void setCookie(ResponseCookie... cookie) {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		HttpServletResponse response = servletRequestAttributes.getResponse();

		for (ResponseCookie responseCookie : cookie) {
			response.addHeader(SET_COOKIE, responseCookie.toString());
		}
	}

	public static void deleteCookieFromRequest(String... cookieName) {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();

		for (String cookie : cookieName) {
			if (Objects.nonNull(request.getCookies())) {
				ResponseCookie deleteCookie = ResponseCookie.from(cookie, "")
					.path("/")
					.maxAge(0)
					.httpOnly(true)
					.build();

				JwtCookieProvider.setCookie(deleteCookie);
			}
		}
	}

}
