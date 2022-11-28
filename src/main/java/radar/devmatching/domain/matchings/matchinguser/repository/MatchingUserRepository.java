package radar.devmatching.domain.matchings.matchinguser.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;

public interface MatchingUserRepository extends JpaRepository<MatchingUser, Long> {

	boolean existsByMatchingIdAndUserId(Long matchingId, Long userId);

	void deleteByMatchingIdAndUserId(Long matchingId, Long userId);
}
