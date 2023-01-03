package radar.devmatching.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.exception.DuplicateException;
import radar.devmatching.domain.user.exception.EmptySpaceException;
import radar.devmatching.domain.user.repository.UserRepository;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.request.UpdateUserRequest;
import radar.devmatching.domain.user.service.dto.response.SimpleUserResponse;
import radar.devmatching.domain.user.service.dto.response.UserResponse;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserResponse createUser(CreateUserRequest request) {

		// TODO : 예외 설정하기
		if (!request.getNickNameCheck() || !request.getUsernameCheck()) {
			throw new RuntimeException();
		}

		User signUpUser = CreateUserRequest.toEntity(request, passwordEncoder);

		userRepository.save(signUpUser);
		log.info("create user={}", signUpUser);

		return UserResponse.of(signUpUser);
	}

	@Override
	public UserResponse getUser(Long userId) {
		User user = getUserEntity(userId);
		log.info("get user info={}", user);
		return UserResponse.of(user);
	}

	@Override
	public SimpleUserResponse getSimpleUser(User authUser) {
		log.info("get simple user info={}", authUser);
		return SimpleUserResponse.of(authUser);
	}

	@Override
	public UserResponse getUserByUsername(String username) {
		User user = getUserEntityByUsername(username);
		log.info("signIn user={}", user);
		return UserResponse.of(user);
	}

	/**
	 * TODO : nickName 변경하는거는 api 분리할 예정
	 */
	@Override
	@Transactional
	public UserResponse updateUser(UpdateUserRequest request, Long userId) {

		User user = getUserEntity(userId);

		user.update(request.getSchoolName(), request.getGithubUrl(), request.getIntroduce());

		log.info("update user data:{}", user);
		return UserResponse.of(user);
	}

	@Override
	@Transactional
	public void deleteUser(Long userId) {

		log.info("delete user={}", userId);
		userRepository.deleteById(userId);
	}

	/**
	 * 회원 가입시에만 사용
	 */
	@Override
	public void checkDuplicateUsername(CreateUserRequest request) {

		String username = request.getUsername();
		if (!StringUtils.hasText(username)) {
			throw new EmptySpaceException(ErrorMessage.EMPTY_USERNAME);
		}

		request.usernameDuplicateCheckClear();
		userRepository.findByUsername(username).ifPresent(user -> {
			throw new DuplicateException(ErrorMessage.DUPLICATE_USERNAME);
		});
		request.usernameNonDuplicate();
		log.info("checkDuplicateUsername request info={}", request);
	}

	@Override
	public void checkDuplicateNickName(CreateUserRequest request) {
		String nickName = request.getNickName();
		if (!StringUtils.hasText(nickName)) {
			throw new EmptySpaceException(ErrorMessage.EMPTY_NICKNAME);
		}

		request.nickNameDuplicateCheckClear();
		userRepository.findByNickName(nickName).ifPresent(user -> {
			throw new DuplicateException(ErrorMessage.DUPLICATE_NICKNAME);
		});
		request.nickNameNonDuplicate();
		log.info("checkDuplicateNickName request info={}", request);
	}

	public User getUserEntity(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
	}

	public User getUserEntityByUsername(String username) {
		return userRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
	}

}
