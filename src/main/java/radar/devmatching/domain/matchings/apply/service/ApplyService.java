package radar.devmatching.domain.matchings.apply.service;

import java.util.List;

import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyResponse;

public interface ApplyService {

	Apply createApply(Long simplePostId, Long userId);

	List<ApplyResponse> getAllApplyList(Long userId);

	int getAcceptedApplyCount(Long simplePostId);

	Apply findById(Long applyId);

}
