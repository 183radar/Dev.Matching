package radar.devmatching.common.security.jwt;

import static org.springframework.http.HttpHeaders.*;

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
import radar.devmatching.common.exception.BusinessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.common.security.JwtCookieProvider;
import radar.devmatching.common.security.JwtProperties;
import radar.devmatching.common.security.jwt.exception.ExpiredAccessTokenException;
import radar.devmatching.common.security.jwt.exception.ExpiredRefreshTokenException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtCookieProvider jwtCookieProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String accessToken = null;
		try {
			// accessToken 유효성 검사
			accessToken = authentication(request);
		} catch (ExpiredAccessTokenException e) {
			accessToken = refreshAuthentication(request, response);
		}

		if (Objects.nonNull(accessToken)) {
			Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}

	private String authentication(HttpServletRequest request) {
		String accessToken = jwtCookieProvider.getCookieFromRequest(request, JwtProperties.ACCESS_TOKEN_HEADER);
		log.info("access Token = {}", accessToken);
		if (Objects.isNull(accessToken)) {
			throw new BusinessException(ErrorMessage.ACCESS_TOKEN_NOT_FOUND);
		}
		jwtTokenProvider.validAccessToken(accessToken);
		return accessToken;
	}

	/**
	 * TODO : refreshToken 검사해서 유효하지 않으면 예외를 던질지 아니면 토큰을 다시 생성할지 결정하기.
	 * accessToken, refreshToken 재생성 or signOut 요청후 토큰 만료후 재로그인 하기.
	 * TODO : 코드 중복 부분 리펙터링하기
	 */
	private String refreshAuthentication(HttpServletRequest request, HttpServletResponse response) {

		String accessToken = null;
		String refreshToken = jwtCookieProvider.getCookieFromRequest(request, JwtProperties.REFRESH_TOKEN_HEADER);

		if (Objects.isNull(refreshToken)) {
			throw new BusinessException(ErrorMessage.REFRESH_TOKEN_NOT_FOUND);
		}

		try {
			// refreshToken 유효성 검사
			jwtTokenProvider.validRefreshToken(refreshToken);

			accessToken = jwtTokenProvider.createNewAccessTokenFromRefreshToken(refreshToken);

			ResponseCookie accessTokenCookie =
				jwtCookieProvider.createCookie(JwtProperties.ACCESS_TOKEN_HEADER, accessToken,
					jwtTokenProvider.getExpireTime());

			response.addHeader(SET_COOKIE, accessTokenCookie.toString());

		} catch (ExpiredRefreshTokenException e) {
			// refreshToken 만료됐을때 accessToken, refreshToken 재생성
			accessToken = jwtTokenProvider.createNewAccessTokenFromRefreshToken(refreshToken);
			refreshToken = jwtTokenProvider.createNewRefreshToken(refreshToken);

			ResponseCookie accessTokenCookie = jwtCookieProvider.createCookie(JwtProperties.ACCESS_TOKEN_HEADER,
				accessToken,
				jwtTokenProvider.getExpireTime());
			ResponseCookie refreshTokenCookie = jwtCookieProvider.createCookie(JwtProperties.REFRESH_TOKEN_HEADER,
				refreshToken,
				jwtTokenProvider.getExpireTime());

			response.setHeader(SET_COOKIE, accessTokenCookie.toString());
			response.addHeader(SET_COOKIE, refreshTokenCookie.toString());
		}
		return accessToken;
	}
}
