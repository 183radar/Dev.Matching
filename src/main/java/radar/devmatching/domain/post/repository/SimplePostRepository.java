package radar.devmatching.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.post.entity.SimplePost;

public interface SimplePostRepository extends JpaRepository<SimplePost, Long> {

	// 필요할 수도 있을것 같아서 만듦
	// @Query("select s from SimplePost s left join fetch s.fullPost where s.id = :simplePostId")
	// Optional<SimplePost> findPost(Long simplePostId);
}
