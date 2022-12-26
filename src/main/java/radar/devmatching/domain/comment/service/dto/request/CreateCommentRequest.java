package radar.devmatching.domain.comment.service.dto.request;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Builder;
import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.entity.SubComment;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;

public class CreateCommentRequest {

	@NotBlank
	@Length(max = 10000)
	private final String content;

	private final CommentType commentType;

	@Builder
	public CreateCommentRequest(String content, CommentType commentType) {
		this.content = content;
		this.commentType = commentType;
	}

	public static CreateCommentRequest of(CommentType commentType) {
		return new CreateCommentRequest(null, commentType);
	}

	public MainComment toMainCommentEntity(SimplePost simplePost, User user) {
		return MainComment.builder()
			.fullPost(
				simplePost.getFullPost()
			).comment(
				Comment.builder()
					.content(this.content)
					.user(user)
					.build()
			).build();
	}

	public SubComment toSubCommentEntity(MainComment mainComment, User user) {
		return SubComment.builder()
			.mainComment(mainComment)
			.comment(
				Comment.builder()
					.content(this.content)
					.user(user)
					.build()
			).build();
	}

	public String getContent() {
		return content;
	}

	public CommentType getCommentType() {
		return commentType;
	}

	public enum CommentType {
		MAIN, SUB
	}
}
