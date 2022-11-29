package radar.devmatching.domain.post.service.dto.response;

import java.time.LocalDateTime;

import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;

public class SimplePostResponse {

	private final String title;

	private final PostCategory category;

	private final Region region;

	private final Integer userNum;

	private final LocalDateTime createDate;

	// private Long clickNum; 조회수는 나중에 추가할게

	public SimplePostResponse(String title, PostCategory category, Region region, Integer userNum,
		LocalDateTime createDate) {
		this.title = title;
		this.category = category;
		this.region = region;
		this.userNum = userNum;
		this.createDate = createDate;
	}

	public static SimplePostResponse of(SimplePost simplePost) {
		return new SimplePostResponse(simplePost.getTitle(), simplePost.getCategory(),
			simplePost.getRegion(), simplePost.getUserNum(), simplePost.getCreateDate());
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

	public LocalDateTime getCreateDate() {
		return createDate;
	}
}
