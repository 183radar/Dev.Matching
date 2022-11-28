package radar.devmatching.domain.matchings.matching.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.common.entity.BaseEntity;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.post.entity.SimplePost;

@Table(name = "MATCHING")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Matching extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "matching_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	private SimplePost simplePost;

	// Matching 삭제시 MatchingUser 삭제
	@OneToMany(mappedBy = "matching", orphanRemoval = true)
	private List<MatchingUser> matchingUsers;

	@Builder
	public Matching(Long id) {
		this.id = id;
		matchingUsers = new ArrayList<>();
	}

	public void setSimplePost(SimplePost simplePost) {
		this.simplePost = simplePost;
	}
}
