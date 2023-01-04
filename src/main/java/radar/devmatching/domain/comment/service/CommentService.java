package radar.devmatching.domain.comment.service;

import java.util.List;

import radar.devmatching.domain.comment.service.dto.UpdateCommentDto;
import radar.devmatching.domain.comment.service.dto.request.CreateCommentRequest;
import radar.devmatching.domain.comment.service.dto.response.MainCommentResponse;

public interface CommentService {
	void createMainComment(long simplePostId, long loginUserId, CreateCommentRequest createCommentRequest);

	long createSubComment(long mainCommentId, long loginUserId, CreateCommentRequest createCommentRequest);

	List<MainCommentResponse> getAllComments(long fullPostId);

	void mainCommentExistById(long mainCommentId);

	UpdateCommentDto getMainCommentOnly(long mainCommentId);

	UpdateCommentDto getSubCommentOnly(long subCommentId);

	long updateMainComment(long mainCommentId, UpdateCommentDto updateCommentDto, long loginUserId);

	long updateSubComment(long subCommentId, UpdateCommentDto updateCommentDto, long loginUserId);

	Long deleteMainComment(long mainCommentId, long loginUserId);

	Long deleteSubComment(long subCommentId, long loginUserId);
}
