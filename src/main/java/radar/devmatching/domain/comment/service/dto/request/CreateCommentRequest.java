package radar.devmatching.domain.comment.service.dto.request;

import lombok.Builder;
import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.entity.SubComment;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;

public class CreateCommentRequest {

	private String content;

	private CommentType commentType;

	@Builder
	private CreateCommentRequest(String content) {
		this.content = content;
	}

	private CreateCommentRequest(CommentType commentType) {
		this.commentType = commentType;
	}

	public static CreateCommentRequest of(CommentType commentType) {
		return new CreateCommentRequest(commentType);
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
