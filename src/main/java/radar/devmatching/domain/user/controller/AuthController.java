package radar.devmatching.domain.user.controller;

import static org.springframework.http.HttpHeaders.*;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.JwtCookieProvider;
import radar.devmatching.common.security.jwt.JwtToken;
import radar.devmatching.domain.user.service.AuthService;
import radar.devmatching.domain.user.service.dto.request.SignInRequest;
import radar.devmatching.domain.user.service.dto.response.SignInResponse;
import radar.devmatching.domain.user.service.dto.response.SignOutResponse;

/**
 *  TODO : RestController -> Controller 변경
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final JwtCookieProvider jwtCookieProvider;

	// TODO : SecurityContextHolder 에 Authentication 저장하기 추가.
	@PostMapping("/signIn")
	public void signIn(HttpServletResponse response, @ModelAttribute SignInRequest signInRequest) {
		SignInResponse signInResponse = authService.signIn(signInRequest.getUsername(), signInRequest.getPassword());

		JwtToken accessToken = signInResponse.getAccessToken();
		JwtToken refreshToken = signInResponse.getRefreshToken();

		ResponseCookie accessTokenCookie = jwtCookieProvider.createCookie(accessToken.getHeader(),
			accessToken.getToken(),
			accessToken.getExpireTime());
		ResponseCookie refreshTokenCookie = jwtCookieProvider.createCookie(refreshToken.getHeader(),
			refreshToken.getToken(),
			refreshToken.getExpireTime());

		response.setHeader(SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(SET_COOKIE, refreshTokenCookie.toString());

		// return "/";
	}

	@GetMapping("/signOut")
	public void signOut(HttpServletResponse response) {
		SignOutResponse signOutResponse = authService.singOut();

		ResponseCookie accessTokenCookie = ResponseCookie.from(signOutResponse.getAccessTokenHeader(), "")
			.path("/")
			.maxAge(0)
			.httpOnly(true)
			.build();
		ResponseCookie refreshTokenCookie = ResponseCookie.from(signOutResponse.getRefreshTokenHeader(), "")
			.path("/")
			.maxAge(0)
			.httpOnly(true)
			.build();
		response.setHeader(SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(SET_COOKIE, refreshTokenCookie.toString());
	}

}
