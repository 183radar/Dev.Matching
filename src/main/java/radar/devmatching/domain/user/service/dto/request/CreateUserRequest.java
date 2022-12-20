package radar.devmatching.domain.user.service.dto.request;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.user.entity.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

	Boolean usernameCheck;

	Boolean nickNameCheck;

	@Builder
	public CreateUserRequest(String username, String password, String nickName, String schoolName) {
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.schoolName = schoolName;
		this.usernameCheck = false;
		this.nickNameCheck = false;
	}

	public static CreateUserRequest of() {
		return new CreateUserRequest();
	}

	public static User toEntity(CreateUserRequest request, PasswordEncoder passwordEncoder) {
		return User.builder()
			.username(request.getUsername())
			.password(passwordEncoder.encode(request.getPassword()))
			.nickName(request.getNickName())
			.schoolName(request.getSchoolName())
			.build();
	}

	public void usernameNonDuplicate() {
		this.usernameCheck = true;
	}

	public void nickNameNonDuplicate() {
		this.nickNameCheck = true;
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

	public Boolean getUsernameCheck() {
		return usernameCheck;
	}

	public void setUsernameCheck(Boolean usernameCheck) {
		this.usernameCheck = usernameCheck;
	}

	public Boolean getNickNameCheck() {
		return nickNameCheck;
	}

	public void setNickNameCheck(Boolean nickNameCheck) {
		this.nickNameCheck = nickNameCheck;
	}
}
