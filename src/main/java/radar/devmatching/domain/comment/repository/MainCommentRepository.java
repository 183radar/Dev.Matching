package radar.devmatching.domain.comment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.repository.custom.MainCommentCustomRepository;

public interface MainCommentRepository extends JpaRepository<MainComment, Long>, MainCommentCustomRepository {

	@EntityGraph(attributePaths = {"comment"})
	Optional<MainComment> findMainCommentById(Long mainCommentId);
}
