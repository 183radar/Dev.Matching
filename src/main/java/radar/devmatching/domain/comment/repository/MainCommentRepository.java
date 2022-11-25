package radar.devmatching.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.repository.custom.MainCommentCustomRepository;

public interface MainCommentRepository extends JpaRepository<MainComment, Long>, MainCommentCustomRepository {
}
