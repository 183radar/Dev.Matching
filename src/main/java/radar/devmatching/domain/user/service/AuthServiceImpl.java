package radar.devmatching.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.UsernamePasswordNotMatchException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.common.security.JwtProperties;
import radar.devmatching.common.security.jwt.JwtTokenCookieInfo;
import radar.devmatching.common.security.jwt.JwtTokenProvider;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.dto.response.SignInResponse;
import radar.devmatching.domain.user.service.dto.response.SignOutResponse;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public SignInResponse signIn(String username, String password) {

		User findUser;
		try {
			findUser = userService.getUserEntityByUsername(username);
		} catch (EntityNotFoundException e) {
			log.warn("username not found : username = {}", username);
			throw new UsernamePasswordNotMatchException(ErrorMessage.AUTHENTICATION_FAIL);
		}
		if (!passwordEncoder.matches(password, findUser.getPassword())) {
			log.warn("request password not match about found password : access username = {}", username);
			throw new UsernamePasswordNotMatchException(ErrorMessage.AUTHENTICATION_FAIL);
		}
		log.info("findUser role = {}", findUser.getUserRole());
		String accessToken = jwtTokenProvider.createAccessToken(username, findUser.getUserRole());
		String refreshToken = jwtTokenProvider.createRefreshToken(username, findUser.getUserRole());

		return SignInResponse.builder()
			.accessToken(JwtTokenCookieInfo.builder()
				.header(JwtProperties.ACCESS_TOKEN_HEADER)
				.token(accessToken)
				.expireTime(jwtTokenProvider.getExpireTime())
				.build())
			.refreshToken(JwtTokenCookieInfo.builder()
				.header(JwtProperties.REFRESH_TOKEN_HEADER)
				.token(refreshToken)
				.expireTime(jwtTokenProvider.getExpireTime())
				.build())
			.build();
	}

	@Override
	public SignOutResponse signOut() {
		return SignOutResponse.builder()
			.accessTokenHeader(JwtProperties.ACCESS_TOKEN_HEADER)
			.refreshTokenHeader(JwtProperties.REFRESH_TOKEN_HEADER)
			.build();
	}
}
