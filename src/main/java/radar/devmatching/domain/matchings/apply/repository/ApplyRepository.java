package radar.devmatching.domain.matchings.apply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.matchings.apply.entity.Apply;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

	List<Apply> findAllByApplySimplePostId(Long simplePostId);

}
