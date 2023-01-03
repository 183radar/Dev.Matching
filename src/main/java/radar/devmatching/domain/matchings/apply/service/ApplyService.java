package radar.devmatching.domain.matchings.apply.service;

import java.util.List;

import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyResponse;
import radar.devmatching.domain.user.entity.User;

public interface ApplyService {

	Apply createApply(Long simplePostId, User authUser);

	List<ApplyResponse> getAllApplyList(User authUser);

	int getAcceptedApplyCount(Long simplePostId);

	Apply getApply(Long applyId);

}
