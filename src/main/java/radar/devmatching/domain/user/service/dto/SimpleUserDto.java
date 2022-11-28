package radar.devmatching.domain.user.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.user.entity.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleUserDto {

	private String username;

	private String nickname;

	private String schoolName;

	private String githubUrl;

	private String introduce;

	@Builder(access = AccessLevel.PRIVATE)
	private SimpleUserDto(String username, String nickname, String schoolName, String githubUrl, String introduce) {
		this.username = username;
		this.nickname = nickname;
		this.schoolName = schoolName;
		this.githubUrl = githubUrl;
		this.introduce = introduce;
	}

	public static SimpleUserDto of() {
		return new SimpleUserDto();
	}

	public static SimpleUserDto of(User user) {
		return SimpleUserDto.builder()
			.username(user.getUsername())
			.nickname(user.getNickName())
			.schoolName(user.getSchoolName())
			.githubUrl(user.getGithubUrl())
			.introduce(user.getIntroduce())
			.build();
	}
}
