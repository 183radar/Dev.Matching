package radar.devmatching.domain.post.simple.service.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import radar.devmatching.common.util.ExcludeJacocoGenerated;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatePostRequest {

	@NotBlank
	@Length(max = 200)
	private String title;

	@NotNull
	private PostCategory category;

	@NotNull
	private Region region;

	@NotNull
	@Range(min = 2, max = 100)
	private Integer userNum;

	@NotBlank
	@Length(max = 10000)
	private String content;

	@Builder
	public CreatePostRequest(String title, PostCategory category, Region region, Integer userNum, String content) {
		this.title = title;
		this.category = category;
		this.region = region;
		this.userNum = userNum;
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
			.userNum(userNum)
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

	public Integer getUserNum() {
		return userNum;
	}

	public String getContent() {
		return content;
	}

	@ExcludeJacocoGenerated
	@Override
	public String toString() {
		return "CreatePostRequest{" +
			"title='" + title + '\'' +
			", category=" + category.getName() +
			", region=" + region.getName() +
			", userNum=" + userNum +
			", content='" + content + '\'' +
			'}';
	}
}
