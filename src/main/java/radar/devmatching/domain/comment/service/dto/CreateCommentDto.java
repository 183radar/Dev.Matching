package radar.devmatching.domain.comment.service.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCommentDto {

	private String content;

	public static CreateCommentDto of() {
		return new CreateCommentDto();
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
}
