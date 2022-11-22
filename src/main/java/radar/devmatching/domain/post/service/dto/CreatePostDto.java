package radar.devmatching.domain.post.service.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatePostDto {

	private String title;

	private PostCategory category;

	private Region region;

	private String content;

	public static CreatePostDto of() {
		return new CreatePostDto();
	}

	public SimplePost toEntity() {
		return SimplePost.builder()
			.title(this.title)
			.category(this.category)
			.region(this.region)
			.userNum(1)
			.fullPost(
				FullPost.builder().
					content(this.content)
					.build()
			).build();
	}
}
