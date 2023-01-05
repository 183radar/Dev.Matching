package radar.devmatching.domain.comment.service.dto;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import radar.devmatching.common.util.ExcludeJacocoGenerated;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.entity.SubComment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateCommentDto {

	private Long entityId;

	@NotBlank
	@Length(max = 10000)
	private String content;

	private CommentType commentType;

	@Builder
	public UpdateCommentDto(Long entityId, String content, CommentType commentType) {
		this.entityId = entityId;
		this.content = content;
		this.commentType = commentType;
	}

	public static UpdateCommentDto of(long mainCommentId, MainComment mainComment, CommentType commentType) {
		return new UpdateCommentDto(mainCommentId, mainComment.getComment().getContent(), commentType);
	}

	public static UpdateCommentDto of(long subCommentId, SubComment subComment, CommentType commentType) {
		return new UpdateCommentDto(subCommentId, subComment.getComment().getContent(), commentType);
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public CommentType getCommentType() {
		return commentType;
	}

	public void setCommentType(CommentType commentType) {
		this.commentType = commentType;
	}

	public String getContent() {
		return content;
	}

	@ExcludeJacocoGenerated
	@Override
	public String toString() {
		return "UpdateCommentDto{" +
			"entityId=" + entityId +
			", content='" + content +
			'}';
	}

	public enum CommentType {
		MAIN, SUB
	}
}
