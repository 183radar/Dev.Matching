package radar.devmatching.domain.matchings.apply.service;

import java.util.List;

import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyLeaderResponse;

public interface ApplyLeaderService {

	void acceptApply(Long userId, Long applyId, Long simplePostId);

	void denyApply(Long userId, Long applyId, Long simplePostId);

	List<ApplyLeaderResponse> getAllApplyList(Long userId, Long simplePostId);
}
