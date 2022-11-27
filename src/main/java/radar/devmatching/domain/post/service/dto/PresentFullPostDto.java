package radar.devmatching.domain.post.service.dto;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PresentFullPostDto {

	private String title;

	private PostCategory category;

	private Region region;

	private Integer userNum;

	private LocalDateTime createDate;

	private String content;

	// private Long clickNum; 조회수는 나중에 추가할게

	@Builder
	private PresentFullPostDto(String title, PostCategory category, Region region, Integer userNum,
		LocalDateTime createDate,
		String content) {
		this.title = title;
		this.category = category;
		this.region = region;
		this.userNum = userNum;
		this.createDate = createDate;
		this.content = content;
	}

	public static PresentFullPostDto of(SimplePost simplePost) {
		return PresentFullPostDto.builder()
			.title(simplePost.getTitle())
			.category(simplePost.getCategory())
			.region(simplePost.getRegion())
			.userNum(simplePost.getUserNum())
			.createDate(simplePost.getCreateDate())
			.content(simplePost.getFullPost().getContent())
			.build();
	}
}
