package radar.devmatching.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import radar.devmatching.domain.post.entity.SimplePost;

public interface SimplePostRepository extends JpaRepository<SimplePost, Long> {

	List<SimplePost> findMyPostByUserId(Long userId);

	@Query("select a.applySimplePost from Apply a where a.applyUser.id = :userId")
	List<SimplePost> findApplicationPosts(Long userId);
}
