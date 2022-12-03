package radar.devmatching.domain.post.service.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatePostRequest {

	private String title;

	private PostCategory category;

	private Region region;

	private String content;

	@Builder
	private CreatePostRequest(String title, PostCategory category, Region region, String content) {
		this.title = title;
		this.category = category;
		this.region = region;
		this.content = content;
	}

	public static CreatePostRequest of() {
		return new CreatePostRequest();
	}

	public SimplePost toEntity(User user, Matching matching) {
		return SimplePost.builder()
			.title(this.title)
			.category(this.category)
			.region(this.region)
			.userNum(1)
			.leader(user)
			.matching(matching)
			.fullPost(
				FullPost.builder().
					content(this.content)
					.build()
			).build();
	}
}
