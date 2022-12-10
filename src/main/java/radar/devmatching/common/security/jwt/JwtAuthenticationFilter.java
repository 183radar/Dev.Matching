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

	//setFilterProcessesUrl 설정으로 로그인 url 변경가능

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String accessToken = null;
		try {
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
		String accessToken = request.getHeader(JwtProperties.ACCESS_TOKEN_HEADER);
		if (Objects.isNull(accessToken)) {
			throw new BusinessException(ErrorMessage.ACCESS_TOKEN_NOT_FOUND);
		}
		jwtTokenProvider.validAccessToken(accessToken);
		return accessToken;
	}

	private String refreshAuthentication(HttpServletRequest request, HttpServletResponse response) {

		String accessToken = null;
		String refreshToken = request.getHeader(JwtProperties.REFRESH_TOKEN_HEADER);

		if (Objects.isNull(refreshToken)) {
			throw new BusinessException(ErrorMessage.REFRESH_TOKEN_NOT_FOUND);
		}

		try {

			jwtTokenProvider.validRefreshToken(refreshToken);
			log.info("refresh token valid");

			accessToken = jwtTokenProvider.createNewAccessTokenFromRefreshToken(refreshToken);
			log.info("access token create");

			ResponseCookie accessTokenCookie =
				jwtCookieProvider.createCookie(JwtProperties.ACCESS_TOKEN_HEADER, accessToken,
					jwtTokenProvider.getExpireTime());

			response.addHeader(SET_COOKIE, accessTokenCookie.toString());
			log.info("access token set cookie");

		} catch (ExpiredRefreshTokenException e) {
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
