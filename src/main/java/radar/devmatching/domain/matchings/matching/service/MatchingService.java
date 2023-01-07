package radar.devmatching.domain.matchings.matching.service;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.service.dto.MatchingUpdate;
import radar.devmatching.domain.matchings.matching.service.dto.response.MatchingInfoResponse;

public interface MatchingService {

	MatchingInfoResponse getMatchingInfo(Long matchingId, Long userId);

	MatchingUpdate getMatchingUpdateData(Long matchingId);

	void updateMatching(Long matchingId, MatchingUpdate matchingUpdate);

	Matching findById(Long matchingId);
}
