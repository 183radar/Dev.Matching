package radar.devmatching.domain.comment.service;

import java.util.List;

import radar.devmatching.domain.comment.service.dto.CreateCommentDto;
import radar.devmatching.domain.comment.service.dto.MainCommentResponse;
import radar.devmatching.domain.user.entity.User;

public interface CommentService {
	void createMainComment(long simplePostId, User loginUser, CreateCommentDto createCommentDto);

	List<MainCommentResponse> getAllComments(long fullPostId);
}
