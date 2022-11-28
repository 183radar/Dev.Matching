package radar.devmatching.domain.matchings.matchinguser.service;

public interface MatchingUserService {

	void createMatchingUser(Long userId, Long matchingId);

	void deleteMatchingUser(Long userId, Long matchingId);
}
