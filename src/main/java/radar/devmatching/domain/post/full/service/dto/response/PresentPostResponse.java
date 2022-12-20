package radar.devmatching.domain.post.full.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import radar.devmatching.domain.comment.service.dto.response.MainCommentResponse;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;

@Builder(access = AccessLevel.PRIVATE)
public class PresentPostResponse {

	private final String title;

	private final PostCategory category;

	private final Region region;

	private final Integer userNum;

	private final Long clickCount;

	private final LocalDateTime createDate;

	private final Integer applyCount;

	private final String content;

	private final List<MainCommentResponse> mainCommentResponse;

	public static PresentPostResponse of(SimplePost simplePost, int applyCount,
		List<MainCommentResponse> mainCommentResponse) {
		return PresentPostResponse.builder()
			.title(simplePost.getTitle())
			.category(simplePost.getCategory())
			.region(simplePost.getRegion())
			.userNum(simplePost.getUserNum())
			.clickCount(simplePost.getClickCount())
			.createDate(simplePost.getCreateDate())
			.applyCount(applyCount)
			.content(simplePost.getFullPost().getContent())
			.mainCommentResponse(mainCommentResponse)
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

	public Long getClickCount() {
		return clickCount;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public Integer getApplyCount() {
		return applyCount;
	}

	public String getContent() {
		return content;
	}

	public List<MainCommentResponse> getMainCommentResponse() {
		return mainCommentResponse;
	}
}
