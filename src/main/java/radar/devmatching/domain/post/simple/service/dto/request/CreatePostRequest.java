package radar.devmatching.domain.post.simple.service.dto.request;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatePostRequest {

	// @NotBlank
	@Length(max = 200)
	private String title;

	// @NotNull
	private PostCategory category;

	// @NotNull
	private Region region;

	@NotBlank
	@Length(max = 10000)
	private String content;

	@Builder
	public CreatePostRequest(String title, PostCategory category, Region region, String content) {
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

	public String getTitle() {
		return title;
	}

	public PostCategory getCategory() {
		return category;
	}

	public Region getRegion() {
		return region;
	}

	public String getContent() {
		return content;
	}
}
