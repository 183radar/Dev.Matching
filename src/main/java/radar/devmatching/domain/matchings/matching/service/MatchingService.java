package radar.devmatching.domain.matchings.matching.service;

import radar.devmatching.domain.matchings.matching.service.dto.MatchingUpdate;
import radar.devmatching.domain.matchings.matching.service.dto.response.MatchingResponse;
import radar.devmatching.domain.user.entity.User;

public interface MatchingService {

	MatchingResponse getMatchingInfo(Long matchingId, User user);

	MatchingUpdate getMatchingUpdateData(Long matchingId);

	void updateMatching(Long matchingId, MatchingUpdate matchingUpdate);
}
