package radar.devmatching.domain.matchings.apply.service;

import java.util.List;

import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyLeaderResponse;
import radar.devmatching.domain.user.entity.User;

public interface ApplyLeaderService {

	void acceptApply(User authUser, Long applyId, Long simplePostId);

	void denyApply(User authUser, Long applyId, Long simplePostId);

	List<ApplyLeaderResponse> getAllApplyList(User authUser, Long simplePostId);
}
