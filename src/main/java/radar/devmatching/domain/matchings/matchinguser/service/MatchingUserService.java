package radar.devmatching.domain.matchings.matchinguser.service;

import java.util.List;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.service.dto.response.MatchingUserResponse;
import radar.devmatching.domain.user.entity.User;

public interface MatchingUserService {

	MatchingUser createMatchingUser(Matching matching, User user);

	List<MatchingUserResponse> getMatchingUserList(Long userId);

	void deleteMatchingUser(Long userId, Long matchingId);
}
