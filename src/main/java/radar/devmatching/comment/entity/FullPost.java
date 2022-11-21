package radar.devmatching.comment.entity;

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
import radar.devmatching.post.entity.MainComment;

@Table(name = "FULL_POST")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FullPost extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "full_post_id")
	private Long id;

	private String content;

	@OneToOne(fetch = FetchType.LAZY)
	private SimplePost simplePost;

	@OneToMany(mappedBy = "fullPost", orphanRemoval = true)
	private List<MainComment> mainComments;

	@Builder
	public FullPost(String content) {
		this.content = content;
		this.mainComments = new ArrayList<>();
	}

	public void setSimplePost(SimplePost simplePost) {
		this.simplePost = simplePost;
	}
}
