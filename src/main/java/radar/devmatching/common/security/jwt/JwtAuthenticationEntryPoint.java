package radar.devmatching.common.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.security.jwt.exception.TokenException;

/**
 * EntryPoint는 토큰을 인증하는 과정에서 발생하는 Exception을 처리하기 위해 만들었음
 * 토큰 인증과정에서 오류가 발생했을 때 처리하지 않은 예외 :
 * 예외 처리할 방법 생각해야함.
 * 쿠키를 지우고 로그인페이지로 리다이렉트 시키는 방법 괜찮아 보임.
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint {

	public void commence(HttpServletRequest request, HttpServletResponse response,
		TokenException tokenException) throws IOException, ServletException {
		log.warn("Access User's Token is Invalid : ");
		log.warn("exception info={}", tokenException.getErrorMessage());
		// response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.sendRedirect("/api/users/signIn");
	}
}
