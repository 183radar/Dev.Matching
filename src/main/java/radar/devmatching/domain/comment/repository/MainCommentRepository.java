package radar.devmatching.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.comment.entity.MainComment;

public interface MainCommentRepository extends JpaRepository<MainComment, Long> {
}
