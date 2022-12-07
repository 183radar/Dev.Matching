package radar.devmatching.domain.comment.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import radar.devmatching.domain.post.entity.FullPost;

@Table(name = "MAIN_COMMENT")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MainComment extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "main_comment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "full_post_id")
	private FullPost fullPost;

	//Comment의 생명주기를 MainComment가 관리한다. MainComment 저장 시 Comment도 같이 저장되며 삭제도 동일하다.
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Comment comment;

	//MainComment 삭제 시 SubComment도 자동으로 삭제한다.
	@OneToMany(mappedBy = "mainComment", orphanRemoval = true)
	private List<SubComment> subComments;

	@Builder
	public MainComment(FullPost fullPost, Comment comment) {
		this.fullPost = fullPost;
		fullPost.getMainComments().add(this);
		this.comment = comment;
		// comment.setMainComment(this);
		this.subComments = new ArrayList<>();
	}

	public void update(String content) {
		this.comment.updateContent(content);
	}
}
