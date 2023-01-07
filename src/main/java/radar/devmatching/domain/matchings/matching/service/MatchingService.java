package radar.devmatching.domain.matchings.matching.service;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.service.dto.MatchingInfo;
import radar.devmatching.domain.matchings.matching.service.dto.MatchingUpdate;

public interface MatchingService {

	MatchingInfo getMatchingInfo(Long matchingId, Long userId);

	MatchingUpdate getMatchingUpdateData(Long matchingId);

	void updateMatching(Long matchingId, MatchingUpdate matchingUpdate);

	Matching findById(Long matchingId);
}
