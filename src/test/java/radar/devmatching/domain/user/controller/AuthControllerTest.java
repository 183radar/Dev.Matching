package radar.devmatching.domain.user.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import radar.devmatching.common.security.JwtProperties;
import radar.devmatching.common.security.jwt.JwtTokenCookieInfo;
import radar.devmatching.domain.user.service.AuthService;
import radar.devmatching.domain.user.service.dto.response.SignInResponse;
import radar.devmatching.domain.user.service.dto.response.SignOutResponse;
import radar.devmatching.util.ControllerTestSetUp;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController의")
public class AuthControllerTest extends ControllerTestSetUp {

	private static final String BASIC_URL = "/api/users";
	private static final String SIGN_IN_REQUEST = "signInRequest";

	@MockBean
	AuthService authService;

	@Test
	@DisplayName("signInPage 메서드는 모델에 빈 SignInRequest 객체를 넣어준다.")
	void signInPage() throws Exception {
		//given
		MockHttpServletRequestBuilder request = get(BASIC_URL + "/signIn");
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isOk())
			.andExpect(handler().handlerType(AuthController.class))
			.andExpect(handler().methodName("signInPage"))
			.andExpect(model().attributeExists(SIGN_IN_REQUEST))
			.andDo(print());
	}

	@Test
	@DisplayName("로그아웃이 정상적으로 진행되면 쿠키도 지워진다.")
	void signOut() throws Exception {
		//given
		SignOutResponse signOutResponse = SignOutResponse.builder()
			.accessTokenHeader(JwtProperties.ACCESS_TOKEN_HEADER)
			.refreshTokenHeader(JwtProperties.REFRESH_TOKEN_HEADER)
			.build();
		given(authService.signOut()).willReturn(signOutResponse);

		MockHttpServletRequestBuilder request = get(BASIC_URL + "/signOut")
			.with(SecurityMockMvcRequestPostProcessors.csrf());
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isFound())
			.andExpect(redirectedUrl(BASIC_URL + "/signIn"))
			.andExpect(handler().handlerType(AuthController.class))
			.andExpect(handler().methodName("signOut"))
			.andExpect(cookie().doesNotExist(JwtProperties.ACCESS_TOKEN_HEADER))
			.andExpect(cookie().doesNotExist(JwtProperties.REFRESH_TOKEN_HEADER))
			.andDo(print());
	}

	@Nested
	@DisplayName("signIn 메서드에서")
	class SignInMethod {

		@Test
		@DisplayName("정상 흐름이면 쿠키에 토큰을 설정해준다.")
		void signIn() throws Exception {
			//given
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("username", "username");
			params.add("password", "password");

			given(authService.signIn(anyString(), anyString())).willReturn(getSignInResponse());
			MockHttpServletRequestBuilder request = post(BASIC_URL + "/signIn")
				.params(params)
				.with(SecurityMockMvcRequestPostProcessors.csrf());
			//when
			ResultActions result = mockMvc.perform(request);
			//then
			result.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"))
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("signIn"))
				.andExpect(cookie().exists(JwtProperties.ACCESS_TOKEN_HEADER))
				.andExpect(cookie().exists(JwtProperties.REFRESH_TOKEN_HEADER))
				.andDo(print());
		}

		@Test
		@DisplayName("username이 공백이면 로그인 페이지로 리다이렉트한다.")
		void usernameBlank() throws Exception {
			//given
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("username", "");
			params.add("password", "password");

			MockHttpServletRequestBuilder request = post(BASIC_URL + "/signIn")
				.params(params)
				.with(SecurityMockMvcRequestPostProcessors.csrf());
			//when
			ResultActions result = mockMvc.perform(request);
			//then
			result.andExpect(status().isOk())
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("signIn"))
				.andDo(print());
		}

		@Test
		@DisplayName("password가 공백이면 로그인 페이지로 리다이렉트한다.")
		void passwordBlank() throws Exception {
			//given
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("username", "username");
			params.add("password", "");

			MockHttpServletRequestBuilder request = post(BASIC_URL + "/signIn")
				.params(params)
				.with(SecurityMockMvcRequestPostProcessors.csrf());
			//when
			ResultActions result = mockMvc.perform(request);
			//then
			result.andExpect(status().isOk())
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("signIn"))
				.andDo(print());
		}

		private SignInResponse getSignInResponse() {
			return SignInResponse.builder()
				.accessToken(JwtTokenCookieInfo.builder()
					.header(JwtProperties.ACCESS_TOKEN_HEADER)
					.token("test")
					.expireTime(1000)
					.build())
				.refreshToken(JwtTokenCookieInfo.builder()
					.header(JwtProperties.REFRESH_TOKEN_HEADER)
					.token("test")
					.expireTime(1000)
					.build())
				.build();
		}
	}

}
