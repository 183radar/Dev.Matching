package radar.devmatching.comment.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.common.entity.BaseEntity;

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

	//SimplePost가 FullPost의 생명주기를 관리한다. (FullPost 삭제 시 Comment들도 전부 자동 삭제됨)
	@OneToOne(mappedBy = "simplePost", cascade = CascadeType.ALL, orphanRemoval = true)
	private FullPost fullPost;

	@Builder
	public SimplePost(String title, PostCategory category, Region region, Integer userNum, PostState postState,
		FullPost fullPost) {
		this.title = title;
		this.category = category;
		this.region = region;
		this.userNum = userNum;
		this.postState = postState;
		this.fullPost = fullPost;
		fullPost.setSimplePost(this);
	}
}
