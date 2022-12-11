package radar.devmatching.domain.post.full.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.post.full.entity.FullPost;

public interface FullPostRepository extends JpaRepository<FullPost, Long> {

}
