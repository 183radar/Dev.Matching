package radar.devmatching.domain.user.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.exception.DuplicateException;
import radar.devmatching.domain.user.exception.EmptySpaceException;
import radar.devmatching.domain.user.repository.UserRepository;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.request.UpdateUserRequest;
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
	public UserResponse getUser(Long requestUserId, User authUser) {
		validatePermission(requestUserId, authUser);
		log.info("get user info={}", authUser);
		return UserResponse.of(authUser);
	}

	@Override
	public UserResponse getUserByUsername(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
		log.info("signIn user={}", user);
		return UserResponse.of(user);
	}

	/**
	 * TODO : nickName 변경하는거는 api 분리할 예정
	 */
	@Override
	@Transactional
	public UserResponse updateUser(UpdateUserRequest request, Long requestUserId, User authUser) {
		validatePermission(requestUserId, authUser);

		User user = userRepository.findById(requestUserId).get();

		user.changeSchoolName(request.getSchoolName());
		user.changeGithubUrl(request.getGithubUrl());
		user.changeIntroduce(request.getIntroduce());
		log.info("update user data:{}", user);
		return UserResponse.of(user);
	}

	@Override
	@Transactional
	public void deleteUser(Long requestUserId, User authUser) {
		validatePermission(requestUserId, authUser);
		log.info("delete user={}", authUser);
		userRepository.delete(authUser);
	}

	private void changeNickName(String nickName, User user) {
		if (user.getNickName().equals(nickName)) {
			return;
		}
		checkDuplicateNickName(nickName, user.getId());
		user.changeNickName(nickName);
	}

	/**
	 * 회원 가입시에만 사용
	 */
	@Override
	public void checkDuplicateUsername(CreateUserRequest request) {
		log.info("checkDuplicateUsername request info={}", request);
		String username = request.getUsername();
		if (!StringUtils.hasText(username)) {
			throw new EmptySpaceException(ErrorMessage.EMPTY_USERNAME);
		}
		request.usernameDuplicateCheckClear();
		userRepository.findByUsername(username).ifPresent(user -> {
			throw new DuplicateException(ErrorMessage.DUPLICATE_USERNAME);
		});
		request.usernameNonDuplicate();
	}

	/**
	 * 닉네임 중복 확인
	 * BusinessException 정의되면 예외 변경 예정
	 */
	@Override
	public void checkDuplicateNickName(String nickName, Long userId) {
		userRepository.findByNickName(nickName).ifPresent(user -> {
			if (!Objects.equals(user.getId(), userId) || userId == null) {
				throw new DuplicateException(ErrorMessage.DUPLICATE_NICKNAME);
			}
		});
	}

	@Override
	public void checkDuplicateNickName(CreateUserRequest request) {
		log.info("checkDuplicateNickName request info={}", request);
		String nickName = request.getNickName();
		if (!StringUtils.hasText(nickName)) {
			throw new EmptySpaceException(ErrorMessage.EMPTY_NICKNAME);
		}

		request.nickNameDuplicateCheckClear();
		userRepository.findByNickName(nickName).ifPresent(user -> {
			throw new DuplicateException(ErrorMessage.DUPLICATE_NICKNAME);
		});
		request.nickNameNonDuplicate();
	}

	/**
	 * 사용자가 접근하는 userId가 사용자 아이디와 일치하는지 감사
	 * @param requestUserId
	 * @param authUser
	 */
	private void validatePermission(Long requestUserId, User authUser) {
		if (!Objects.equals(requestUserId, authUser.getId())) {
			throw new InvalidAccessException(ErrorMessage.INVALID_ACCESS);
		}
	}

}
