package radar.devmatching.domain.comment.service;

import java.util.List;

import radar.devmatching.domain.comment.service.dto.UpdateCommentDto;
import radar.devmatching.domain.comment.service.dto.request.CreateCommentRequest;
import radar.devmatching.domain.comment.service.dto.response.MainCommentResponse;
import radar.devmatching.domain.user.entity.User;

public interface CommentService {
	void createMainComment(long simplePostId, User loginUser, CreateCommentRequest createCommentRequest);

	long createSubComment(long mainCommentId, User loginUser, CreateCommentRequest createCommentRequest);

	List<MainCommentResponse> getAllComments(long fullPostId);

	void mainCommentExistById(long mainCommentId);

	UpdateCommentDto getMainCommentOnly(long mainCommentId);

	UpdateCommentDto getSubCommentOnly(long subCommentId);

	long updateMainComment(long mainCommentId, UpdateCommentDto updateCommentDto, User loginUser);

	long updateSubComment(long subCommentId, UpdateCommentDto updateCommentDto, User loginUser);

	Long deleteMainComment(long mainCommentId, User loginUser);

	Long deleteSubComment(long subCommentId, User loginUser);
}
