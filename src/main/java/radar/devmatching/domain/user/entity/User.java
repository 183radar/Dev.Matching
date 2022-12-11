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
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import radar.devmatching.common.entity.BaseEntity;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.post.simple.entity.SimplePost;

@Table(name = "USERS")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "user_id")
	private Long id;

	@Column(name = "username")
	private String username;

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

	// User 삭제 시 SimplePost도 삭제
	@OneToMany(mappedBy = "leader", orphanRemoval = true)
	private List<SimplePost> simplePosts;

	@Builder
	public User(Long id, String username, String password, String nickName, String schoolName, String githubUrl,
		String introduce) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.schoolName = schoolName;
		this.githubUrl = githubUrl;
		this.introduce = introduce;
		this.userRole = UserRole.ROLE_USER;
		this.matchingUsers = new ArrayList<>();
		this.applyList = new ArrayList<>();
		this.simplePosts = new ArrayList<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		User user = (User)o;

		if (getId() != null ? !getId().equals(user.getId()) : user.getId() != null)
			return false;
		if (getUsername() != null ? !getUsername().equals(user.getUsername()) : user.getUsername() != null)
			return false;
		if (getPassword() != null ? !getPassword().equals(user.getPassword()) : user.getPassword() != null)
			return false;
		if (getNickName() != null ? !getNickName().equals(user.getNickName()) : user.getNickName() != null)
			return false;
		return getSchoolName() != null ? getSchoolName().equals(user.getSchoolName()) : user.getSchoolName() == null;
	}

	@Override
	public int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
		result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
		result = 31 * result + (getNickName() != null ? getNickName().hashCode() : 0);
		result = 31 * result + (getSchoolName() != null ? getSchoolName().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "User{" +
			"id=" + id +
			", username='" + username + '\'' +
			", password='" + password + '\'' +
			", nickName='" + nickName + '\'' +
			", schoolName='" + schoolName + '\'' +
			", githubUrl='" + githubUrl + '\'' +
			", introduce='" + introduce + '\'' +
			", userRole=" + userRole +
			'}';
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

	public List<MatchingUser> getMatchingUsers() {
		return matchingUsers;
	}

	public List<Apply> getApplyList() {
		return applyList;
	}

	public List<SimplePost> getSimplePosts() {
		return simplePosts;
	}

	public void changeNickName(String nickName) {
		this.nickName = nickName;
	}

	public void changeSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public void changeGithubUrl(String githubUrl) {
		this.githubUrl = githubUrl;
	}

	public void changeIntroduce(String introduce) {
		this.introduce = introduce;
	}
}
