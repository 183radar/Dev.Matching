package radar.devmatching.domain.comment.service.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.post.entity.SimplePost;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDto {

	private String content;

	public static CommentDto of() {
		return new CommentDto();
	}

	public MainComment toMainCommentEntity(SimplePost simplePost) {
		return MainComment.builder()
			.fullPost(
				simplePost.getFullPost()
			).comment(
				Comment.builder().content(this.content).build()
			).build();
	}
}
