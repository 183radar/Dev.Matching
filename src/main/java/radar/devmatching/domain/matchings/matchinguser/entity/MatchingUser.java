package radar.devmatching.domain.matchings.matchinguser.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.common.entity.BaseEntity;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.user.entity.User;

@Table(name = "MATCHING_USER")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingUser extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "matching_user_id")
	private Long id;

	@Enumerated(value = EnumType.STRING)
	private MatchingUserRole matchingUserRole;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "matching_id")
	private Matching matching;

	@Builder
	public MatchingUser(MatchingUserRole matchingUserRole, User user, Matching matching) {
		this.matchingUserRole = matchingUserRole;
		this.user = user;
		user.getMatchingUsers().add(this);
		this.matching = matching;
		matching.getMatchingUsers().add(this);
	}
}
