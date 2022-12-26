package radar.devmatching.domain.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.security.JwtCookieProvider;
import radar.devmatching.common.security.jwt.JwtToken;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.AuthService;
import radar.devmatching.domain.user.service.dto.request.SignInRequest;
import radar.devmatching.domain.user.service.dto.response.SignInResponse;
import radar.devmatching.domain.user.service.dto.response.SignOutResponse;

/**
 *  TODO : RestController -> Controller 변경
 */
@Slf4j
@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@GetMapping("/signIn")
	public String signInPage(Model model) {
		model.addAttribute("signInRequest", SignInRequest.of());
		return "/user/signIn";
	}

	// TODO : SecurityContextHolder 에 Authentication 저장하기 추가.
	@PostMapping("/signIn")
	public String signIn(HttpServletResponse response, @ModelAttribute SignInRequest signInRequest,
		BindingResult bindingResult) {
		SignInResponse signInResponse = authService.signIn(signInRequest.getUsername(), signInRequest.getPassword());

		JwtToken accessToken = signInResponse.getAccessToken();
		JwtToken refreshToken = signInResponse.getRefreshToken();

		ResponseCookie accessTokenCookie = JwtCookieProvider.createCookie(accessToken.getHeader(),
			accessToken.getToken(),
			accessToken.getExpireTime());
		ResponseCookie refreshTokenCookie = JwtCookieProvider.createCookie(refreshToken.getHeader(),
			refreshToken.getToken(),
			refreshToken.getExpireTime());

		JwtCookieProvider.setCookie(response, accessTokenCookie);
		JwtCookieProvider.setCookie(response, refreshTokenCookie);
		log.info("signIn execute");
		return "redirect:/";
	}

	@GetMapping("/signOut")
	public String signOut(HttpServletRequest request, HttpServletResponse response, @AuthUser User user) {
		SignOutResponse signOutResponse = authService.singOut();
		log.info("access User={}", user);
		log.info("logout process execute");
		JwtCookieProvider.deleteCookieFromRequest(request, response, signOutResponse.getAccessTokenHeader());
		JwtCookieProvider.deleteCookieFromRequest(request, response, signOutResponse.getRefreshTokenHeader());

		return "redirect:/api/users/signIn";
	}

}
