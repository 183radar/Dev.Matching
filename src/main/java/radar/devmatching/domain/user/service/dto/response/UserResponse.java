package radar.devmatching.domain.user.service.dto.response;

import lombok.Builder;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.entity.UserRole;

public class UserResponse {

	private final Long id;
	private final String username;
	private final String password;
	private final String nickName;
	private final String schoolName;
	private final String githubUrl;
	private final String introduce;
	private final UserRole userRole;

	@Builder
	public UserResponse(Long id, String username, String password, String nickName, String schoolName, String githubUrl,
		String introduce, UserRole userRole) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.schoolName = schoolName;
		this.githubUrl = githubUrl;
		this.introduce = introduce;
		this.userRole = userRole;
	}

	public static UserResponse of(User user) {
		return UserResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.password(user.getPassword())
			.nickName(user.getNickName())
			.schoolName(user.getSchoolName())
			.githubUrl(user.getGithubUrl())
			.introduce(user.getIntroduce())
			.build();
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getNickName() {
		return nickName;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public String getGithubUrl() {
		return githubUrl;
	}

	public String getIntroduce() {
		return introduce;
	}

	public UserRole getUserRole() {
		return userRole;
	}
}
