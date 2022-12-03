package radar.devmatching.domain.post.service.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdatePostRequest {

	private String title;

	private PostCategory category;

	private Region region;

	private Integer userNum;

	private String content;

	@Builder
	private UpdatePostRequest(String title, PostCategory category, Region region, Integer userNum, String content) {
		this.title = title;
		this.category = category;
		this.region = region;
		this.userNum = userNum;
		this.content = content;
	}

	public static UpdatePostRequest of(SimplePost simplePost) {
		return UpdatePostRequest.builder()
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
