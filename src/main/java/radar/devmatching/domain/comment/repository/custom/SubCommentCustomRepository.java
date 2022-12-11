package radar.devmatching.domain.comment.repository.custom;

public interface SubCommentCustomRepository {

	Long findBySimplePostIdAsSubCommentId(Long subCommentId);
}
