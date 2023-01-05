package radar.devmatching.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

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
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.exception.DuplicateException;
import radar.devmatching.domain.user.exception.EmptySpaceException;
import radar.devmatching.domain.user.repository.UserRepository;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.request.UpdateUserRequest;
import radar.devmatching.domain.user.service.dto.response.SimpleUserResponse;
import radar.devmatching.domain.user.service.dto.response.UserResponse;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService의")
public class UserServiceTest {

	static final Long TEST_USER_ID = 1L;
	static final String EMPTY_SPACE = "";

	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	private UserService userService;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(userRepository, passwordEncoder);
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

	private CreateUserRequest getCreateUserRequest() {
		CreateUserRequest request = CreateUserRequest.builder()
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("schoolName")
			.build();

		request.usernameNonDuplicate();
		request.nickNameNonDuplicate();

		return request;
	}

	@Nested
	@DisplayName("createUser 메서드에서")
	class CreateUserMethod {

		@Test
		@DisplayName("예외를 던지지 않고 user가 저장된다.")
		void createUserWithoutException() {
			//given
			CreateUserRequest request = getCreateUserRequest();
			given(passwordEncoder.encode(any())).willReturn(request.getPassword());
			User user = CreateUserRequest.toEntity(request, passwordEncoder);

			//when
			UserResponse saveUser = userService.createUser(request);
			//then
			assertThat(saveUser).usingRecursiveComparison().isEqualTo(UserResponse.of(user));
		}

		@Test
		@DisplayName("username 중복 확인하지 않으면 예외를 던진다.")
		void throwDuplicateExceptionAboutUsername() {
			//given
			CreateUserRequest request = getCreateUserRequest();
			request.usernameDuplicateCheckClear();
			//when
			//then
			assertThatThrownBy(() -> userService.createUser(request))
				.isInstanceOf(DuplicateException.class);
		}

		@Test
		@DisplayName("nickName 중복되면 예외를 던진다.")
		void throwDuplicateExceptionAboutNickName() {
			//given
			CreateUserRequest request = getCreateUserRequest();
			request.nickNameDuplicateCheckClear();
			//when
			//then
			assertThatThrownBy(() -> userService.createUser(request))
				.isInstanceOf(DuplicateException.class);

		}

	}

	@Nested
	@DisplayName("getUser 메서드에서")
	class GetUserMethod {

		@Test
		@DisplayName("예외를 던지지 않고 User 정보를 가져온다.")
		void getUserWithoutException() {
			//given
			User authUser = createUser();
			given(userRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
			//when
			UserResponse getUser = userService.getUser(authUser.getId());
			//then
			assertThat(getUser).usingRecursiveComparison().isEqualTo(UserResponse.of(authUser));
		}

	}

	@Nested
	@DisplayName("getSimpleUser 메서드에서")
	class GetSimpleUserMethod {

		@Test
		@DisplayName("예외를 던지지 않고 User 정보를 가져온다.")
		void getSimpleUserWithoutException() {
			//given
			User authUser = createUser();
			given(userRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
			//when
			SimpleUserResponse getUser = userService.getSimpleUser(authUser.getId());
			//then
			assertThat(getUser).usingRecursiveComparison().isEqualTo(SimpleUserResponse.of(authUser));
		}

	}

	@Nested
	@DisplayName("getUserByUsername 메서드에서")
	class GetUserByUsernameMethod {

		@Test
		@DisplayName("예외를 던지지 않고 User 정보를 가져온다.")
		void getUserByUsernameWithoutException() {
			//given
			User authUser = createUser();
			given(userRepository.findByUsername(authUser.getUsername())).willReturn(Optional.of(authUser));
			//when
			UserResponse getUser = userService.getUserByUsername(authUser.getUsername());
			//then
			assertThat(getUser).usingRecursiveComparison().isEqualTo(UserResponse.of(authUser));
		}

	}

	@Nested
	@DisplayName("updateUser 메서드에서")
	class UpdateUserMethod {

		@Test
		@DisplayName("예외를 던지지 않고 User 정보를 변경한다.")
		void updateUserWithoutException() {
			//given
			User user = createUser();
			UpdateUserRequest request = getUpdateUserRequest();
			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
			//when
			UserResponse userResponse = userService.updateUser(request, user.getId());
			//then
			assertThat(userResponse.getSchoolName()).isEqualTo(request.getSchoolName());
			assertThat(userResponse.getGithubUrl()).isEqualTo(request.getGithubUrl());
			assertThat(userResponse.getIntroduce()).isEqualTo(request.getIntroduce());
		}

		private UpdateUserRequest getUpdateUserRequest() {
			return UpdateUserRequest.builder()
				.schoolName("updateSchoolName")
				.githubUrl("updateGithubUrl")
				.introduce("updateIntroduce")
				.build();
		}

	}

	@Nested
	@DisplayName("deleteUser 메서드에서")
	class DeleteUserMethod {

		@Test
		@DisplayName("정상적으로 user가 삭제된다.")
		void deleteUserWithoutException() {
			//given
			User user = createUser();
			//when
			userService.deleteUser(user.getId());
			//then
			verify(userRepository, times(1)).deleteById(user.getId());
		}
	}

	@Nested
	@DisplayName("checkDuplicateUsername 메서드에서")
	class CheckDuplicateUsernameMethod {

		@Test
		@DisplayName("username이 중복되지 않으면 정상 반환된다.")
		void checkDuplicateUsernameWithoutException() {
			//given
			CreateUserRequest request = getCreateUserRequest();
			given(userRepository.findByUsername(request.getUsername())).willReturn(Optional.empty());
			//when
			userService.checkDuplicateUsername(request);
			//then
			assertThat(request.getUsernameCheck()).isTrue();
		}

		@Test
		@DisplayName("username이 공백으로 입력되면 예외를 던진다")
		void requestUsernameIsEmpty() {
			//given
			CreateUserRequest request = getCreateUserRequest();
			request.setUsername(EMPTY_SPACE);
			//when
			//then
			assertThatThrownBy(() -> userService.checkDuplicateUsername(request))
				.isInstanceOf(EmptySpaceException.class);
		}

		@Test
		@DisplayName("username이 중복되면 예외를 던진다")
		void requestUsernameIsDuplicate() {
			//given
			CreateUserRequest request = getCreateUserRequest();
			given(userRepository.findByUsername(request.getUsername())).willReturn(Optional.of(createUser()));
			//when
			//then
			assertThatThrownBy(() -> userService.checkDuplicateUsername(request))
				.isInstanceOf(DuplicateException.class);
		}
	}

	@Nested
	@DisplayName("checkDuplicateNickName 메서드에서")
	class CheckDuplicateNickNameMethod {

		@Test
		@DisplayName("nickName이 중복되지 않으면 정상 반환된다.")
		void checkDuplicateNickNameWithoutException() {
			//given
			CreateUserRequest request = getCreateUserRequest();
			given(userRepository.findByNickName(request.getNickName())).willReturn(Optional.empty());
			//when
			userService.checkDuplicateNickName(request);
			//then
			assertThat(request.getNickNameCheck()).isTrue();
		}

		@Test
		@DisplayName("nickName이 공백으로 입력되면 예외를 던진다")
		void requestNickNameIsEmpty() {
			//given
			CreateUserRequest request = getCreateUserRequest();
			request.setNickName(EMPTY_SPACE);
			//when
			//then
			assertThatThrownBy(() -> userService.checkDuplicateNickName(request))
				.isInstanceOf(EmptySpaceException.class);
		}

		@Test
		@DisplayName("nickName이 중복되면 예외를 던진다")
		void requestNickNameIsDuplicate() {
			//given
			CreateUserRequest request = getCreateUserRequest();
			given(userRepository.findByNickName(request.getNickName())).willReturn(Optional.of(createUser()));
			//when
			//then
			assertThatThrownBy(() -> userService.checkDuplicateNickName(request))
				.isInstanceOf(DuplicateException.class);
		}
	}

	@Nested
	@DisplayName("getUserEntity 메서드에서")
	class GetUserEntityMethod {

		@Test
		@DisplayName("userId에 해당하는 엔티티가 존재한다")
		void getUserEntity() {
			//given
			User user = createUser();
			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
			//when
			User getUserEntity = userService.getUserEntity(user.getId());
			//then
			assertThat(getUserEntity).isEqualTo(user);
		}

		@Test
		@DisplayName("userId에 해당하는 엔티티가 없으면 예외를 던진다.")
		void entityNotFound() {
			//given
			User user = createUser();
			given(userRepository.findById(user.getId())).willReturn(Optional.empty());
			//when
			//then
			assertThatThrownBy(() -> userService.getUserEntity(user.getId()))
				.isInstanceOf(EntityNotFoundException.class);
		}

	}

	@Nested
	@DisplayName("getUserEntityByUsername 메서드에서")
	class GetUserEntityByUsernameMethod {

		@Test
		@DisplayName("username에 해당하는 엔티티가 존재한다")
		void getUserEntityByUsername() {
			//given
			User user = createUser();
			given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
			//when
			User getUserEntityByUsername = userService.getUserEntityByUsername(user.getUsername());
			//then
			assertThat(getUserEntityByUsername).isEqualTo(user);
		}

		@Test
		@DisplayName("username에 해당하는 엔티티가 없으면 예외를 던진다.")
		void entityNotFound() {
			//given
			User user = createUser();
			given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.empty());
			//when
			//then
			assertThatThrownBy(() -> userService.getUserEntityByUsername(user.getUsername()))
				.isInstanceOf(EntityNotFoundException.class);
		}

	}

}

