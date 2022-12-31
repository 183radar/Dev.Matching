package radar.devmatching.domain.matchings.apply.service.dto.response;

import lombok.Builder;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.entity.ApplyState;
import radar.devmatching.domain.post.simple.entity.PostState;
import radar.devmatching.domain.post.simple.entity.SimplePost;

public class ApplyResponse {

	private final Long simplePostId;
	private final String simplePostTitle;
	private final PostState postState;
	private final ApplyState applyState;

	@Builder
	public ApplyResponse(Long simplePostId, String simplePostTitle, PostState postState, ApplyState applyState) {
		this.simplePostId = simplePostId;
		this.simplePostTitle = simplePostTitle;
		this.postState = postState;
		this.applyState = applyState;
	}

	public static ApplyResponse of(Apply apply) {
		SimplePost simplePost = apply.getApplySimplePost();
		return ApplyResponse.builder()
			.simplePostId(simplePost.getId())
			.simplePostTitle(simplePost.getTitle())
			.postState(simplePost.getPostState())
			.applyState(apply.getApplyState())
			.build();
	}

	public Long getSimplePostId() {
		return simplePostId;
	}

	public String getSimplePostTitle() {
		return simplePostTitle;
	}

	public PostState getPostState() {
		return postState;
	}

	public ApplyState getApplyState() {
		return applyState;
	}
}
