package radar.devmatching.domain.user.service.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

	private String nickName;
	private String schoolName;
	private String githubUrl;
	private String introduce;

	@Builder
	public UpdateUserRequest(String nickName, String schoolName, String githubUrl, String introduce) {
		this.nickName = nickName;
		this.schoolName = schoolName;
		this.githubUrl = githubUrl;
		this.introduce = introduce;
	}

}
