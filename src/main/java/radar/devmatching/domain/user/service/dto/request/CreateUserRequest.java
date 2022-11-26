package radar.devmatching.domain.user.service.dto.request;

import lombok.Getter;
import lombok.Setter;
import radar.devmatching.domain.user.entity.User;

@Getter
@Setter
public class CreateUserRequest {

	String username;
	String password;
	String nickName;
	String schoolName;

	public CreateUserRequest(String username, String password, String nickName, String schoolName) {
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.schoolName = schoolName;
	}

	public static User toEntity(CreateUserRequest request) {
		return User.builder()
			.username(request.getUsername())
			.password(request.getPassword())
			.nickName(request.getNickName())
			.schoolName(request.getSchoolName())
			.build();
	}
}
