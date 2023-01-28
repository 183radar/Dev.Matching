package radar.devmatching.common.security.jwt;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.common.security.JwtCookieProvider;
import radar.devmatching.common.security.JwtProperties;
import radar.devmatching.common.security.jwt.exception.ExpiredAccessTokenException;
import radar.devmatching.common.security.jwt.exception.JwtTokenNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		authentication();
		filterChain.doFilter(request, response);
	}

	private void authentication() {
		String accessToken = null;
		try {
			// accessToken 유효성 검사
			accessToken = accessAuthentication();
		} catch (ExpiredAccessTokenException e) {
			accessToken = refreshAuthentication();
		}

		if (Objects.nonNull(accessToken)) {
			Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	}

	private String accessAuthentication() {
		String accessToken = JwtCookieProvider.getCookieFromRequest(JwtProperties.ACCESS_TOKEN_HEADER);
		log.info("access Token = {}", accessToken);
		if (Objects.isNull(accessToken)) {
			throw new JwtTokenNotFoundException(ErrorMessage.ACCESS_TOKEN_NOT_FOUND);
		}
		jwtTokenProvider.validAccessToken(accessToken);
		return accessToken;
	}

	/**
	 * TODO : refreshToken 검사해서 유효하지 않으면 예외를 던질지 아니면 토큰을 다시 생성할지 결정하기.
	 * accessToken, refreshToken 재생성 or signOut 요청후 토큰 만료후 재로그인 하기.
	 * TODO : 코드 중복 부분 리펙터링하기
	 */
	private String refreshAuthentication() {

		String accessToken = null;
		String refreshToken = JwtCookieProvider.getCookieFromRequest(JwtProperties.REFRESH_TOKEN_HEADER);

		/**
		 * refreshToken 유효성 검사
		 * refreshToken 유효하지 않으면 ExpiredRefreshTokenException 던짐
		 */
		jwtTokenProvider.validRefreshToken(refreshToken);

		accessToken = jwtTokenProvider.createNewAccessTokenFromRefreshToken(refreshToken);

		ResponseCookie accessTokenCookie =
			JwtCookieProvider.createCookie(JwtProperties.ACCESS_TOKEN_HEADER, accessToken,
				jwtTokenProvider.getExpireTime());

		JwtCookieProvider.setCookie(accessTokenCookie);

		return accessToken;
	}
}
