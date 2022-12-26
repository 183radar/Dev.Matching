package radar.devmatching.common.security.jwt;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.security.JwtCookieProvider;
import radar.devmatching.common.security.JwtProperties;
import radar.devmatching.common.security.jwt.exception.TokenException;

/**
 * EntryPoint는 토큰을 인증하는 과정에서 발생하는 Exception을 처리하기 위해 만들었음
 * 토큰 인증과정에서 오류가 발생했을 때 처리하지 않은 예외 : TokenException, ExpiredRefreshTokenException, JwtTokenNotFoundException
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint {

	public void commence(HttpServletRequest request, HttpServletResponse response,
		TokenException tokenException) throws IOException {
		log.warn("exception info={}", tokenException.getErrorMessage(), tokenException);

		JwtCookieProvider.deleteCookieFromRequest(request, response, JwtProperties.ACCESS_TOKEN_HEADER);
		JwtCookieProvider.deleteCookieFromRequest(request, response, JwtProperties.REFRESH_TOKEN_HEADER);

		response.sendRedirect("/api/users/signIn");
	}
}
