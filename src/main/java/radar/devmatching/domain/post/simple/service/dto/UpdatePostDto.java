package radar.devmatching.domain.post.simple.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdatePostDto {

	private String title;

	private PostCategory category;

	private Region region;

	private Integer userNum;

	private String content;

	@Builder
	private UpdatePostDto(String title, PostCategory category, Region region, Integer userNum, String content) {
		this.title = title;
		this.category = category;
		this.region = region;
		this.userNum = userNum;
		this.content = content;
	}

	public static UpdatePostDto of(SimplePost simplePost) {
		return UpdatePostDto.builder()
			.title(simplePost.getTitle())
			.category(simplePost.getCategory())
			.region(simplePost.getRegion())
			.userNum(simplePost.getUserNum())
			.content(simplePost.getFullPost().getContent())
			.build();
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
}
