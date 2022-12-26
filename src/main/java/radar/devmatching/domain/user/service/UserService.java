package radar.devmatching.domain.user.service;

import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.request.UpdateUserRequest;
import radar.devmatching.domain.user.service.dto.response.UserResponse;

public interface UserService {

	UserResponse createUser(CreateUserRequest request);

	UserResponse getUser(Long requestUserId, User authUser);

	UserResponse getUserByUsername(String username);

	UserResponse updateUser(UpdateUserRequest request, Long requestUserId, User authUser);

	void deleteUser(Long requestUserId, User authUser);

	void checkDuplicateUsername(String username);

	void checkDuplicateNickName(String nickName, Long userId);
}
