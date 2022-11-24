package radar.devmatching.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
