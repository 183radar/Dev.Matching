package radar.devmatching.domain.comment.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.entity.SubComment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateCommentDto {

	private String content;

	private CommentType commentType;

	@Builder
	private UpdateCommentDto(String content) {
		this.content = content;
	}

	private UpdateCommentDto(String content, CommentType commentType) {
		this.content = content;
		this.commentType = commentType;
	}

	public static UpdateCommentDto of(MainComment mainComment, CommentType commentType) {
		return new UpdateCommentDto(mainComment.getComment().getContent(), commentType);
	}

	public static UpdateCommentDto of(SubComment subComment, CommentType commentType) {
		return new UpdateCommentDto(subComment.getComment().getContent(), commentType);
	}

	public String getContent() {
		return content;
	}

	public enum CommentType {
		MAIN, SUB
	}
}
