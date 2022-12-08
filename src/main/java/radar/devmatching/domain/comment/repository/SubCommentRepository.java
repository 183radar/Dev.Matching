package radar.devmatching.domain.comment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import radar.devmatching.domain.comment.entity.SubComment;
import radar.devmatching.domain.comment.repository.custom.SubCommentCustomRepository;

public interface SubCommentRepository extends JpaRepository<SubComment, Long>, SubCommentCustomRepository {
	@EntityGraph(attributePaths = {"comment", "mainComment", "mainComment.fullPost", "mainComment.fullPost.simplePost"})
	Optional<SubComment> findSubCommentById(Long subCommentId);
}
