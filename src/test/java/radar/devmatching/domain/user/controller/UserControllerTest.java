package radar.devmatching.domain.user.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.response.SimpleUserResponse;
import radar.devmatching.domain.user.service.dto.response.UserResponse;
import radar.devmatching.util.ControllerTestSetUp;

@WebMvcTest(UserController.class)
@DisplayName("UserController의")
public class UserControllerTest extends ControllerTestSetUp {

	static final String BASIC_URL = "/api/users";
	static final String CREATE_USER_REQUEST_DTO = "createUserRequest";
	static final String SIMPLE_USER_INFO = "simpleUserInfo";
	static final String USER_INFO = "userInfo";
	static final Long USER_ID = 1L;
	static final Long USER_ID2 = 2L;

	private User authUserSetUp(Long userId) {
		User user = createUser(userId);
		given(userService.findByUsername(anyString())).willReturn(user);
		return user;
	}

	private CreateUserRequest getCreateUserRequest() {
		CreateUserRequest request = CreateUserRequest.builder()
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("test")
			.build();

		request.usernameNonDuplicate();
		request.nickNameNonDuplicate();

		return request;
	}

	@Test
	@DisplayName("signUp메서드는 세션에 빈 createUserRequest 객체를 넘겨주며 회원가입 페이지로 리다이렉트한다.")
	void signUp() throws Exception {
		//given
		MockHttpSession session = new MockHttpSession();
		MockHttpServletRequestBuilder request = get(BASIC_URL + "/signUp").session(session);
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(BASIC_URL + "/signUp/page"))
			.andExpect(handler().handlerType(UserController.class))
			.andExpect(handler().methodName("signUp"))
			.andDo(print());

		assertThat(session.getAttribute(CREATE_USER_REQUEST_DTO)).isNotNull();
	}

	@Test
	@DisplayName("signUpPage 메서드는 세션에서 createUserRequest 객체를 받아와 모델에 넣어준다.")
	void signUpPage() throws Exception {
		//given
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(CREATE_USER_REQUEST_DTO, CreateUserRequest.of());
		MockHttpServletRequestBuilder request = get(BASIC_URL + "/signUp/page").session(session);
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isOk())
			.andExpect(handler().handlerType(UserController.class))
			.andExpect(handler().methodName("signUpPage"))
			.andExpect(model().attributeExists(CREATE_USER_REQUEST_DTO))
			.andDo(print());

		assertThat(session.getAttribute(CREATE_USER_REQUEST_DTO)).isNotNull();
	}

	@Test
	@DisplayName("checkDuplicateUsername 메서드는 username이 중복이 아닐경우 사용가능 메시지를 보내면서 회원가입 페이지로 이동한다.")
	void checkDuplicateUsernameMethod() throws Exception {
		//given
		CreateUserRequest createUserRequest = getCreateUserRequest();

		MockHttpSession session = new MockHttpSession();
		session.setAttribute(CREATE_USER_REQUEST_DTO, createUserRequest);
		MockHttpServletRequestBuilder request = post(BASIC_URL + "/duplicate/username")
			.session(session)
			.with(SecurityMockMvcRequestPostProcessors.csrf());
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isFound())
			.andExpect(handler().handlerType(UserController.class))
			.andExpect(handler().methodName("checkDuplicateUsername"))
			.andExpect(flash().attribute("msg", createUserRequest.getUsername() + ": 사용가능한 아이디 입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("checkDuplicateNickName 메서드는 nickName이 중복이 아닐경우 사용가능 메시지를 보내면서 회원가입 페이지로 이동한다.")
	void checkDuplicateNickNameMethod() throws Exception {
		//given
		CreateUserRequest createUserRequest = getCreateUserRequest();

		MockHttpSession session = new MockHttpSession();
		session.setAttribute(CREATE_USER_REQUEST_DTO, createUserRequest);
		MockHttpServletRequestBuilder request = post(BASIC_URL + "/duplicate/nickName")
			.session(session)
			.with(SecurityMockMvcRequestPostProcessors.csrf());
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isFound())
			.andExpect(handler().handlerType(UserController.class))
			.andExpect(handler().methodName("checkDuplicateNickName"))
			.andExpect(flash().attribute("msg", createUserRequest.getNickName() + ": 사용가능한 이름 입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("getUser 메서드는 토큰에 저장된 유저 정보를 찾아 보여준다.")
	void getUser() throws Exception {
		//given
		User user = authUserSetUp(USER_ID);
		UserResponse userResponse = UserResponse.of(user);
		given(userService.getUser(anyLong())).willReturn(userResponse);
		MockHttpServletRequestBuilder request = get(BASIC_URL);
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isOk())
			.andExpect(handler().handlerType(UserController.class))
			.andExpect(handler().methodName("getUser"))
			.andDo(print());
	}

	@Test
	@DisplayName("updateUser 메서드는 저장된 토큰으로 사용자 정보 변경 페이지로 이동한다.")
	void updateUser() throws Exception {
		//given
		User user = authUserSetUp(USER_ID);
		given(userService.getUser(eq(USER_ID))).willReturn(UserResponse.of(user));
		MockHttpServletRequestBuilder request = get(BASIC_URL + "/update");
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isOk())
			.andExpect(handler().handlerType(UserController.class))
			.andExpect(handler().methodName("updateUser"))
			.andExpect(model().attributeExists(USER_INFO))
			.andDo(print());
	}

	@Test
	@DisplayName("delete 메서드가 정상 작동하면 로그인 페이지로 리다이렉트한다.")
	void deleteUser() throws Exception {
		//given
		authUserSetUp(USER_ID);
		MockHttpServletRequestBuilder request = get(BASIC_URL + "/delete");
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isFound())
			.andExpect(handler().handlerType(UserController.class))
			.andExpect(handler().methodName("deleteUser"))
			.andExpect(redirectedUrl(BASIC_URL + "/signIn"))
			.andDo(print());

		verify(userService, times(1)).deleteUser(any());
	}

	@Nested
	@DisplayName("updateUser 메서드에서")
	class updateUserMethod {

		@Test
		@DisplayName("정상흐름으로 동작하면 유저 정보 페이지로 리다이렉트한다.")
		void updateUser() throws Exception {
			//given
			User user = authUserSetUp(USER_ID);

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("schoolName", "schoolName");
			params.add("githubUrl", "githubUrl");
			params.add("introduce", "introduce");

			MockHttpServletRequestBuilder request = post(BASIC_URL + "/update")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.params(params);

			//when
			ResultActions result = mockMvc.perform(request);
			//then
			result.andExpect(status().isFound())
				.andExpect(handler().handlerType(UserController.class))
				.andExpect(handler().methodName("updateUser"))
				.andExpect(redirectedUrl(BASIC_URL))
				.andDo(print());
		}

		@Test
		@DisplayName("schoolName이 공백이면 업데이트 페이지로 리다이렉트한다.")
		void schoolNameBlank() throws Exception {
			//given
			User user = authUserSetUp(USER_ID);

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("schoolName", "");
			params.add("githubUrl", "githubUrl");
			params.add("introduce", "introduce");

			MockHttpServletRequestBuilder request = post(BASIC_URL + "/update")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.params(params);

			//when
			ResultActions result = mockMvc.perform(request);
			//then
			result.andExpect(status().isFound())
				.andExpect(handler().handlerType(UserController.class))
				.andExpect(handler().methodName("updateUser"))
				.andExpect(redirectedUrl(BASIC_URL + "/update"))
				.andDo(print());

		}

	}

	@Nested
	@DisplayName("getSimpleUser 메서드에서")
	class GetSimpleUserMethod {

		@Test
		@DisplayName("접속 유저와 요청 userId가 같으면 접속 유저 정보 페이지로 이동한다.")
		void currentUserEqualRequestUserId() throws Exception {
			//given
			authUserSetUp(USER_ID);
			MockHttpServletRequestBuilder request = get(BASIC_URL + "/" + USER_ID);
			//when
			ResultActions result = mockMvc.perform(request);
			//then
			result.andExpect(status().isFound())
				.andExpect(redirectedUrl(BASIC_URL))
				.andExpect(handler().handlerType(UserController.class))
				.andExpect(handler().methodName("getSimpleUser"))
				.andDo(print());
		}

		@Test
		@DisplayName("접속 유저와 요청 userId가 다르면 userId 사용자 정보 페이지로 이동한다.")
		void currentUserNotEqualRequestUserId() throws Exception {
			//given
			User user = authUserSetUp(USER_ID);
			given(userService.getSimpleUser(eq(USER_ID2))).willReturn(SimpleUserResponse.of(createUser(USER_ID2)));
			MockHttpServletRequestBuilder request = get(BASIC_URL + "/" + USER_ID2);
			//when
			ResultActions result = mockMvc.perform(request);
			//then
			result.andExpect(status().isOk())
				.andExpect(handler().handlerType(UserController.class))
				.andExpect(handler().methodName("getSimpleUser"))
				.andExpect(model().attributeExists(SIMPLE_USER_INFO))
				.andDo(print());
		}
	}

	@Nested
	@DisplayName("signUpRequest 메서드에서")
	class SignUpRequestMethod {

		@Test
		@DisplayName("정상 흐름이면 로그인 페이지로 이동한다.")
		void signUpRequest() throws Exception {
			//given
			CreateUserRequest createUserRequest = getCreateUserRequest();
			given(userService.createUser(createUserRequest)).willReturn(UserResponse.of(createUser(USER_ID)));

			MockHttpSession session = new MockHttpSession();
			session.setAttribute(CREATE_USER_REQUEST_DTO, createUserRequest);

			MockHttpServletRequestBuilder request = post(BASIC_URL + "/signUp/page")
				.session(session)
				.with(SecurityMockMvcRequestPostProcessors.csrf());

			//when
			ResultActions result = mockMvc.perform(request);

			//then
			result.andExpect(status().isFound())
				.andExpect(redirectedUrl(BASIC_URL + "/signIn"))
				.andExpect(handler().handlerType(UserController.class))
				.andExpect(handler().methodName("signUpRequest"))
				.andDo(print());

			assertThat(session.getAttribute(CREATE_USER_REQUEST_DTO)).isNull();
		}

		@Nested
		@DisplayName("폼 파라미터에서")
		class CreateUserRequestParam {

			@Test
			@DisplayName("비밀번호가 없으면 회원가입 페이지로 이동한다.")
			void passwordParamEmpty() throws Exception {
				//given
				CreateUserRequest createUserRequest = getCreateUserRequest();
				createUserRequest.setPassword("");

				MockHttpSession session = new MockHttpSession();
				session.setAttribute(CREATE_USER_REQUEST_DTO, createUserRequest);

				MockHttpServletRequestBuilder request = post(BASIC_URL + "/signUp/page")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.session(session);

				//when
				ResultActions result = mockMvc.perform(request);
				//then
				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(UserController.class))
					.andExpect(handler().methodName("signUpRequest"))
					.andExpect(model().attributeHasFieldErrors(CREATE_USER_REQUEST_DTO, "password"))
					.andExpect(model().hasErrors())
					.andDo(print());
			}

			@Test
			@DisplayName("학교이름이 없으면 회원가입 페이지로 이동한다.")
			void schoolNameParamEmpty() throws Exception {
				//given
				CreateUserRequest createUserRequest = getCreateUserRequest();
				createUserRequest.setSchoolName("");

				MockHttpSession session = new MockHttpSession();
				session.setAttribute(CREATE_USER_REQUEST_DTO, createUserRequest);

				MockHttpServletRequestBuilder request = post(BASIC_URL + "/signUp/page")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.session(session);

				//when
				ResultActions result = mockMvc.perform(request);
				//then
				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(UserController.class))
					.andExpect(handler().methodName("signUpRequest"))
					.andExpect(model().attributeHasFieldErrors(CREATE_USER_REQUEST_DTO, "schoolName"))
					.andExpect(model().hasErrors())
					.andDo(print());

			}
		}

	}

}
