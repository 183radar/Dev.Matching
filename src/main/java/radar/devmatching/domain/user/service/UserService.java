package radar.devmatching.domain.user.service;

import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.request.UpdateUserRequest;
import radar.devmatching.domain.user.service.dto.response.SimpleUserResponse;
import radar.devmatching.domain.user.service.dto.response.UserResponse;

public interface UserService {

	UserResponse createUser(CreateUserRequest request);

	UserResponse getUser(Long userId);

	SimpleUserResponse getSimpleUser(User authUser);

	UserResponse getUserByUsername(String username);

	UserResponse updateUser(UpdateUserRequest request, Long userId);

	void deleteUser(Long userId);

	void checkDuplicateUsername(CreateUserRequest request);

	void checkDuplicateNickName(CreateUserRequest request);

	User getUserEntity(Long userId);

	User getUserEntityByUsername(String username);
}
