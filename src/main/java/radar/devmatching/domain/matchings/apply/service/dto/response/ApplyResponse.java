package radar.devmatching.domain.matchings.apply.service.dto.response;

import lombok.Builder;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.entity.ApplyState;
import radar.devmatching.domain.user.entity.User;

public class ApplyResponse {

	private final Long applyId;
	private final String nickName;
	private final String schoolName;
	private final ApplyState state;

	@Builder
	public ApplyResponse(Long applyId, String nickName, String schoolName, ApplyState state) {
		this.applyId = applyId;
		this.nickName = nickName;
		this.schoolName = schoolName;
		this.state = state;
	}

	public static ApplyResponse of(Apply apply) {
		User applyUser = apply.getApplyUser();

		return ApplyResponse.builder()
			.applyId(apply.getId())
			.nickName(applyUser.getNickName())
			.schoolName(applyUser.getSchoolName())
			.state(apply.getApplyState())
			.build();
	}
}
