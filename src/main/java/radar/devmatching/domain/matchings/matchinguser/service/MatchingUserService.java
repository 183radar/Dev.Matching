package radar.devmatching.domain.matchings.matchinguser.service;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.user.entity.User;

public interface MatchingUserService {

	MatchingUser createMatchingUser(Matching matching, User user);

	void deleteMatchingUser(Long userId, Long matchingId);
}
