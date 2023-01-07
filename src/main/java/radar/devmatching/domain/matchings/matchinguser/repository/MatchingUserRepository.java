package radar.devmatching.domain.matchings.matchinguser.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;

public interface MatchingUserRepository extends JpaRepository<MatchingUser, Long> {

	boolean existsByMatchingIdAndUserId(Long matchingId, Long userId);

	void deleteByMatchingIdAndUserId(Long matchingId, Long userId);

	@Query("select m from MatchingUser m where m.user.id = :userId order by m.createDate desc")
	List<MatchingUser> findMatchingUserList(@Param("userId") Long userId);

	Optional<MatchingUser> findByMatchingIdAndUserId(Long matchingId, Long userId);
}
