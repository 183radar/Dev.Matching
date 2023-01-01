package radar.devmatching.domain.user.service;

import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.request.UpdateUserRequest;
import radar.devmatching.domain.user.service.dto.response.SimpleUserResponse;
import radar.devmatching.domain.user.service.dto.response.UserResponse;

public interface UserService {

	UserResponse createUser(CreateUserRequest request);

	UserResponse getUser(User authUser);

	SimpleUserResponse getSimpleUser(User authUser);

	UserResponse getUserByUsername(String username);

	UserResponse updateUser(UpdateUserRequest request, User authUser);

	void deleteUser(User authUser);

	void checkDuplicateUsername(CreateUserRequest request);

	void checkDuplicateNickName(CreateUserRequest request);
}
