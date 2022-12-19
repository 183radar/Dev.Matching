package radar.devmatching.domain.user.service.dto.request;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Builder;
import radar.devmatching.domain.user.entity.User;

public class CreateUserRequest {

	@NotBlank
	@Length(min = 10, max = 30)
	String username;

	@NotBlank
	String password;

	@NotBlank
	String nickName;

	@NotBlank
	String schoolName;

	@Builder
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
}
