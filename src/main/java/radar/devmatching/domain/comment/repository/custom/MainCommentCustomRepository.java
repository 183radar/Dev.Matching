package radar.devmatching.domain.comment.repository.custom;

import java.util.List;

import radar.devmatching.domain.comment.entity.MainComment;

public interface MainCommentCustomRepository {

	List<MainComment> getAllComments(Long fullPostId);

	Long findBySimplePostIdAsMainCommentId(Long mainCommentId);
}
