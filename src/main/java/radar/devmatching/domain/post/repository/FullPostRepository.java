package radar.devmatching.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.post.entity.FullPost;

public interface FullPostRepository extends JpaRepository<FullPost, Long> {

}
