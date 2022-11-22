package radar.devmatching.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.comment.entity.SubComment;

public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
}
