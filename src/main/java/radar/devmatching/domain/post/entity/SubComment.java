package radar.devmatching.domain.post.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.common.entity.BaseEntity;

@Table(name = "SUB_COMMENT")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubComment extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "sub_comment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_comment_id")
	private MainComment mainComment;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Comment comment;

	@Builder
	public SubComment(MainComment mainComment, Comment comment) {
		this.mainComment = mainComment;
		mainComment.getSubComments().add(this);
		this.comment = comment;
		comment.setSubComment(this);
	}
}
