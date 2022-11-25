package radar.devmatching.domain.matchings.matching.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import radar.devmatching.common.entity.BaseEntity;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.post.entity.SimplePost;

@Table(name = "MATCHING")
@Entity
@Getter
// NoArg를 PROTECT로 만든 이유가 내가 생성한 생성자 이외엔 사용 못 하도록 하면서 프록시로 엔티티를 만들어야되니 만든건데
// 이 경우엔 빈 생성자를 생성하니 겹치네. 아래껀 지워도 될 듯?
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
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
	public Matching() {
	}

	public void setSimplePost(SimplePost simplePost) {
		this.simplePost = simplePost;
	}
}
