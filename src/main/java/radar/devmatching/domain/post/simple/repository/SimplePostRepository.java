package radar.devmatching.domain.post.simple.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.PostState;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.repository.custom.SimplePostCustomRepository;

public interface SimplePostRepository extends JpaRepository<SimplePost, Long>, SimplePostCustomRepository {

	List<SimplePost> findMyPostsByLeaderIdOrderByCreateDateDesc(Long userId);

	@Query("select a.applySimplePost from Apply a where a.applyUser.id = :userId order by a.applySimplePost.createDate desc ")
	List<SimplePost> findApplicationPosts(@Param("userId") Long userId);

	@EntityGraph(attributePaths = {"fullPost"})
	Optional<SimplePost> findPostById(Long simplePostId);

	List<SimplePost> findByCategoryAndPostStateOrderByCreateDateDesc(PostCategory category, PostState state);

	List<SimplePost> findByPostStateOrderByCreateDateDesc(PostState state);
}
