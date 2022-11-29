package radar.devmatching.domain.user.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import radar.devmatching.domain.user.entity.User;

@Builder(access = AccessLevel.PRIVATE)
public class SimpleUserResponse {

	private final String username;

	private final String nickname;

	private final String schoolName;

	private final String githubUrl;

	private final String introduce;

	public static SimpleUserResponse of(User user) {
		return SimpleUserResponse.builder()
			.username(user.getUsername())
			.nickname(user.getNickName())
			.schoolName(user.getSchoolName())
			.githubUrl(user.getGithubUrl())
			.introduce(user.getIntroduce())
			.build();
	}

	public String getUsername() {
		return username;
	}

	public String getNickname() {
		return nickname;
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
}
