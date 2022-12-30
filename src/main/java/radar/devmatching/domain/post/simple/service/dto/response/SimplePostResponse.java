package radar.devmatching.domain.post.simple.service.dto.response;

import java.time.LocalDateTime;

import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.PostState;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;

public class SimplePostResponse {

	private final Long simplePostId;

	private final String title;

	private final PostCategory category;

	private final Region region;

	private final Integer userNum;

	private final LocalDateTime createDate;

	private final Long clickCount;

	private final PostState postState;

	public SimplePostResponse(Long simplePostId, String title, PostCategory category, Region region, Integer userNum,
		LocalDateTime createDate, Long clickCount, PostState postState) {
		this.simplePostId = simplePostId;
		this.title = title;
		this.category = category;
		this.region = region;
		this.userNum = userNum;
		this.createDate = createDate;
		this.clickCount = clickCount;
		this.postState = postState;
	}

	public static SimplePostResponse of(SimplePost simplePost) {
		return new SimplePostResponse(simplePost.getId(), simplePost.getTitle(), simplePost.getCategory(),
			simplePost.getRegion(), simplePost.getUserNum(), simplePost.getCreateDate(), simplePost.getClickCount(),
			simplePost.getPostState());
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

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public Long getClickCount() {
		return clickCount;
	}

	public PostState getPostState() {
		return postState;
	}
}
