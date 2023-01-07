package radar.devmatching.domain.matchings.matching.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import radar.devmatching.domain.matchings.matching.entity.Matching;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

	@Query("select ms.matching from MatchingUser ms where ms.user.id = :userId and ms.matching.id = :matchingId")
	Optional<Matching> findByMatchingIdAndUserId(@Param("matchingId") Long matchingId, @Param("userId") Long userId);
}
