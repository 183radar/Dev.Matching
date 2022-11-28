package radar.devmatching.domain.post.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;

@Getter
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

}
