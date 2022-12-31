package radar.devmatching.domain.matchings.apply.service.dto.response;

import lombok.Builder;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.entity.ApplyState;
import radar.devmatching.domain.user.entity.User;

public class ApplyLeaderResponse {

	private final Long applyId;
	private final Long simplePostId;
	private final String nickName;
	private final String schoolName;
	private final ApplyState state;

	@Builder
	public ApplyLeaderResponse(Long applyId, Long simplePostId, String nickName, String schoolName, ApplyState state) {
		this.applyId = applyId;
		this.simplePostId = simplePostId;
		this.nickName = nickName;
		this.schoolName = schoolName;
		this.state = state;
	}

	public static ApplyLeaderResponse of(Apply apply) {
		User applyUser = apply.getApplyUser();

		return ApplyLeaderResponse.builder()
			.simplePostId(apply.getApplySimplePost().getId())
			.applyId(apply.getId())
			.nickName(applyUser.getNickName())
			.schoolName(applyUser.getSchoolName())
			.state(apply.getApplyState())
			.build();
	}

	public Long getSimplePostId() {
		return simplePostId;
	}

	public Long getApplyId() {
		return applyId;
	}

	public String getNickName() {
		return nickName;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public ApplyState getState() {
		return state;
	}
}
