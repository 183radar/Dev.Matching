package radar.devmatching.domain.post.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.common.entity.BaseEntity;

@Table(name = "COMMENT")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "comment_id")
	private Long id;

	// @ManyToOne(fetch = FetchType.LAZY)
	// private User user;

	// 일대일 단방향 관계는 JPA가 지원하지 않기에 양방향 관계로 만듦. 없다고 생각해도 될 듯
	@OneToOne(mappedBy = "comment")
	private MainComment mainComment;
	@OneToOne(mappedBy = "comment")
	private SubComment subComment;

	private String content;

	@Builder
	public Comment(String content) {
		this.content = content;
	}

	public void setMainComment(MainComment mainComment) {
		this.mainComment = mainComment;
	}

	public void setSubComment(SubComment subComment) {
		this.subComment = subComment;
	}
}
