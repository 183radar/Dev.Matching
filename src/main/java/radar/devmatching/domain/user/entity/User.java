package radar.devmatching.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.common.entity.BaseEntity;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "user_id")
	private Long id;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "nickname")
	private String nickName;

	@Column(name = "school_name")
	private String schoolName;

	@Column(name = "github_url")
	private String githubUrl;

	@Column(name = "user_introduce")
	private String introduce;

	@Column(name = "user_role")
	@Enumerated(value = EnumType.STRING)
	private UserRole userRole;

	// User 삭제 시 MatchingUser 도 삭제
	@OneToMany(mappedBy = "user", orphanRemoval = true)
	private List<MatchingUser> matchingUsers;

	// User 삭제 시 Apply도 삭제
	@OneToMany(mappedBy = "applyUser", orphanRemoval = true)
	private List<Apply> applyList;

	@Builder
	public User(String email, String password, String nickName, String schoolName, String githubUrl,
		String introduce) {
		this.email = email;
		this.password = password;
		this.nickName = nickName;
		this.schoolName = schoolName;
		this.githubUrl = githubUrl;
		this.introduce = introduce;
		this.userRole = UserRole.ROLE_USER;
		this.matchingUsers = new ArrayList<>();
		this.applyList = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "User{" +
			"id=" + id +
			", email='" + email + '\'' +
			", password='" + password + '\'' +
			", nickName='" + nickName + '\'' +
			", schoolName='" + schoolName + '\'' +
			", githubUrl='" + githubUrl + '\'' +
			", introduce='" + introduce + '\'' +
			", userRole=" + userRole +
			'}';
	}

}
