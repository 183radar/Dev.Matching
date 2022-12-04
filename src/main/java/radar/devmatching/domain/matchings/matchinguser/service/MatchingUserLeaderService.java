package radar.devmatching.domain.matchings.matchinguser.service;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.user.entity.User;

public interface MatchingUserLeaderService {

	MatchingUser createMatchingUserLeader(User leader, Matching matching);

}
