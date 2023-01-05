package radar.devmatching.domain.post.full.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import radar.devmatching.common.util.ExcludeJacocoGenerated;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdatePostDto {

	private Long simplePostId;

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
	public UpdatePostDto(Long simplePostId, String title, PostCategory category, Region region, Integer userNum,
		String content) {
		this.simplePostId = simplePostId;
		this.title = title;
		this.category = category;
		this.region = region;
		this.userNum = userNum;
		this.content = content;
	}

	public static UpdatePostDto of(SimplePost simplePost) {
		return UpdatePostDto.builder()
			.simplePostId(simplePost.getId())
			.title(simplePost.getTitle())
			.category(simplePost.getCategory())
			.region(simplePost.getRegion())
			.userNum(simplePost.getUserNum())
			.content(simplePost.getFullPost().getContent())
			.build();
	}

	public Long getSimplePostId() {
		return simplePostId;
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
		return "UpdatePostDto{" +
			"simplePostId=" + simplePostId +
			", title='" + title + '\'' +
			", category=" + category.getName() +
			", region=" + region +
			", userNum=" + userNum +
			", content='" + content + '\'' +
			'}';
	}
}
