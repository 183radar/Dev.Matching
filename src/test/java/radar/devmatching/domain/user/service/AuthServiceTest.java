package radar.devmatching.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.UsernamePasswordNotMatchException;
import radar.devmatching.common.security.JwtProperties;
import radar.devmatching.common.security.jwt.JwtTokenCookieInfo;
import radar.devmatching.common.security.jwt.JwtTokenProvider;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.dto.response.SignInResponse;
import radar.devmatching.domain.user.service.dto.response.SignOutResponse;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService의")
public class AuthServiceTest {

	static final Long TEST_USER_ID = 1L;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	UserService userService;

	@Mock
	JwtTokenProvider jwtTokenProvider;

	AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthServiceImpl(passwordEncoder, userService, jwtTokenProvider);
	}

	private User basicUser() {
		return User.builder()
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("schoolName")
			.githubUrl("githubUrl")
			.introduce("introduce")
			.build();
	}

	private User createUser() {
		User user = basicUser();

		ReflectionTestUtils.setField(user, "id", TEST_USER_ID);

		return user;
	}

	@Test
	@DisplayName("signOut 메서드가 정상적으로 실행되면 DTO에 토큰 해더가 설정된다.")
	void signOut() {
		//given
		//when
		SignOutResponse signOutResponse = authService.signOut();
		//then
		assertThat(signOutResponse.getAccessTokenHeader()).isEqualTo(JwtProperties.ACCESS_TOKEN_HEADER);
		assertThat(signOutResponse.getRefreshTokenHeader()).isEqualTo(JwtProperties.REFRESH_TOKEN_HEADER);
	}

	@Nested
	@DisplayName("signIn 메서드에서")
	class SignInMethod {

		@Test
		@DisplayName("username과 password 검증을 정상적으로 통과하면 예외를 던지지 않는다.")
		void signInWithoutException() {
			//given
			User user = createUser();
			given(userService.findByUsername(eq(user.getUsername()))).willReturn(user);
			given(passwordEncoder.matches(any(), any())).willReturn(true);
			//when
			SignInResponse signInResponse = authService.signIn(user.getUsername(), user.getPassword());
			//then
			JwtTokenCookieInfo accessToken = signInResponse.getAccessToken();
			JwtTokenCookieInfo refreshToken = signInResponse.getRefreshToken();
			assertThat(accessToken).isNotNull();
			assertThat(accessToken.getHeader()).isEqualTo(JwtProperties.ACCESS_TOKEN_HEADER);
			assertThat(refreshToken).isNotNull();
			assertThat(refreshToken.getHeader()).isEqualTo(JwtProperties.REFRESH_TOKEN_HEADER);
		}

		@Test
		@DisplayName("username에 해당하는 유저가 없을경우 예외를 던진다.")
		void UserNotFound() {
			//given
			User user = createUser();
			given(userService.findByUsername(user.getUsername())).willThrow(EntityNotFoundException.class);
			//when
			//then
			assertThatThrownBy(() -> authService.signIn(user.getUsername(), user.getPassword()))
				.isInstanceOf(UsernamePasswordNotMatchException.class);
		}

		@Test
		@DisplayName("username에 해당하는 유저의 password와 요청 password가 다를경우 예외를 던진다.")
		void requestPasswordNotMatchUserPassword() {
			//given
			User user = createUser();
			given(userService.findByUsername(eq(user.getUsername()))).willReturn(user);
			given(passwordEncoder.matches(any(), any())).willReturn(false);
			//when
			//then
			assertThatThrownBy(() -> authService.signIn(user.getUsername(), user.getPassword()))
				.isInstanceOf(UsernamePasswordNotMatchException.class);
		}
	}

}
