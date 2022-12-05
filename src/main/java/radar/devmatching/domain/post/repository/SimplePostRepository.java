package radar.devmatching.domain.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import radar.devmatching.domain.post.entity.SimplePost;

public interface SimplePostRepository extends JpaRepository<SimplePost, Long> {

	List<SimplePost> findMyPostsByLeaderIdOrderByCreateDateDesc(long userId);

	@Query("select a.applySimplePost from Apply a where a.applyUser.id = :userId order by a.applySimplePost.createDate desc ")
	List<SimplePost> findApplicationPosts(long userId);

	@EntityGraph(attributePaths = {"fullPost"})
	Optional<SimplePost> findPostById(long simplePostId);
}
