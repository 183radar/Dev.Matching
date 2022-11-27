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
public class PresentSimplePostDto {

	private String title;

	private PostCategory category;

	private Region region;

	private Integer userNum;

	private LocalDateTime createDate;

	// private Long clickNum; 조회수는 나중에 추가할게

	@Builder
	private PresentSimplePostDto(String title, PostCategory category, Region region, Integer userNum,
		LocalDateTime createDate) {
		this.title = title;
		this.category = category;
		this.region = region;
		this.userNum = userNum;
		this.createDate = createDate;
	}

	public static PresentSimplePostDto of(SimplePost simplePost) {
		return PresentSimplePostDto.builder()
			.title(simplePost.getTitle())
			.category(simplePost.getCategory())
			.region(simplePost.getRegion())
			.userNum(simplePost.getUserNum())
			.createDate(simplePost.getCreateDate())
			.build();
	}

	public SimplePost toEntity() {
		return null;
	}
}
