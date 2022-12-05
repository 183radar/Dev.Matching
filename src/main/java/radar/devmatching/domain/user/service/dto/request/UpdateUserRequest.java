package radar.devmatching.domain.user.service.dto.request;

import lombok.Builder;

public class UpdateUserRequest {

	private final String nickName;
	private final String schoolName;
	private final String githubUrl;
	private final String introduce;

	@Builder
	public UpdateUserRequest(String nickName, String schoolName, String githubUrl, String introduce) {
		this.nickName = nickName;
		this.schoolName = schoolName;
		this.githubUrl = githubUrl;
		this.introduce = introduce;
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
}
