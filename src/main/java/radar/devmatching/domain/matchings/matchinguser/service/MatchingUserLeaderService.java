package radar.devmatching.domain.matchings.matchinguser.service;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.user.entity.User;

public interface MatchingUserLeaderService {

	void createMatchingUserLeader(User leader, Matching matching);

}
