package radar.devmatching.domain.matchings.matching.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.matchings.matching.entity.Matching;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
}
