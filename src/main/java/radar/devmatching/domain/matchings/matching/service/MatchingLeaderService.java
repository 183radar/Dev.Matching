package radar.devmatching.domain.matchings.matching.service;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.user.entity.User;

public interface MatchingLeaderService {

	Matching createMatching(User authUser);
}
