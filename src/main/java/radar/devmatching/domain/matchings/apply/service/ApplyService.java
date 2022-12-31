package radar.devmatching.domain.matchings.apply.service;

import java.util.List;

import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyResponse;
import radar.devmatching.domain.user.entity.User;

public interface ApplyService {

	/**
	 * 일반 사용자가 게시글을 통해 신청을 할때 사용
	 * 중복 신청은 안됨.
	 *
	 * @param simplePostId
	 * @param authUser
	 * @return
	 */
	Apply createApply(Long simplePostId, User authUser);

	List<ApplyResponse> getAllApplyList(User authUser);

	int getAcceptedApplyCount(Long simplePostId);

	Apply getApply(Long applyId);

}
