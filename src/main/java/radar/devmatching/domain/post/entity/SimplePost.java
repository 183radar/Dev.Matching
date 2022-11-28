package radar.devmatching.domain.post.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.common.entity.BaseEntity;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.user.entity.User;

@Table(name = "SIMPLE_POST")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimplePost extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "simple_post_id")
	private Long id;

	private String title;

	@Column(name = "post_category")
	@Enumerated(EnumType.STRING)
	private PostCategory category;

	@Enumerated(EnumType.STRING)
	private Region region;

	@Column(name = "user_num")
	private Integer userNum;

	@Column(name = "post_state")
	@Enumerated(EnumType.STRING)
	private PostState postState;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User writer;

	//SimplePost가 FullPost의 생명주기를 관리한다. (FullPost 삭제 시 Comment들도 전부 자동 삭제됨)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private FullPost fullPost;

	//SimplePost가 Matching의 생명주기를 관리한다. (Matching 삭제 시 MatchingUser들도 전부 삭제됨)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Matching matching;

	// simplePost가 Apply의 생명주기를 관리한다.
	@OneToMany(mappedBy = "applySimplePost", orphanRemoval = true)
	private List<Apply> applyList;

	@Builder
	public SimplePost(String title, PostCategory category, Region region, Integer userNum, User writer,
		FullPost fullPost) {
		this.title = title;
		this.category = category;
		this.region = region;
		this.userNum = userNum;
		this.postState = PostState.RECRUITING;
		this.writer = writer;
		writer.getSimplePosts().add(this);
		this.fullPost = fullPost;
		fullPost.setSimplePost(this);

		//SimplePost 생성 시 Matching도 만든다. (같은 생명주기)
		this.matching = Matching.builder().build();
		matching.setSimplePost(this);

		this.applyList = new ArrayList<>();
	}

	public void update(String title, PostCategory category, Region region, Integer userNum, String content) {
		this.title = title;
		this.category = category;
		this.region = region;
		this.userNum = userNum;
		this.fullPost.updateContent(content);
	}
}
