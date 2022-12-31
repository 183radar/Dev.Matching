package radar.devmatching.domain.matchings.apply.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import radar.devmatching.domain.matchings.apply.entity.Apply;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

	List<Apply> findAllByApplySimplePostId(Long simplePostId);

	// TODO : queryDsl 로 바꾸기
	Optional<Apply> findByApplySimplePostIdAndApplyUserId(Long simplePostId, Long userId);

	@Query(value = "select a from Apply a where a.applyUser.id = :userId order by a.createDate desc ")
	List<Apply> findAppliesByUserId(@Param("userId") Long userId);

}
