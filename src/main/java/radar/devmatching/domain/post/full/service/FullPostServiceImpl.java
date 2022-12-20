package radar.devmatching.domain.post.full.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.response.MainCommentResponse;
import radar.devmatching.domain.matchings.apply.service.ApplyService;
import radar.devmatching.domain.post.full.service.dto.UpdatePostDto;
import radar.devmatching.domain.post.full.service.dto.response.PresentPostResponse;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.exception.SimplePostNotFoundException;
import radar.devmatching.domain.post.simple.repository.SimplePostRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FullPostServiceImpl implements FullPostService {

	private final SimplePostRepository simplePostRepository;
	private final ApplyService applyService;
	private final CommentService commentService;

	/**
	 * 게시글 화면 전체를 가져온다(게시글, 신청자 수, 댓글)
	 * @param simplePostId
	 * @return
	 */
	@Override
	public PresentPostResponse getPostWithComment(long simplePostId) {
		SimplePost findPost = simplePostRepository.findPostById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		// 나중에 새로고침 누르면 clickCount는 안 올라가도록 설정해도 좋을듯? (JWT 가져와서)
		findPost.plusClickCount();
		int applyCount = applyService.getAcceptedApplyCount(simplePostId);
		List<MainCommentResponse> allComments = commentService.getAllComments(findPost.getFullPost().getId());

		return PresentPostResponse.of(findPost, applyCount, allComments);
	}

	/**
	 * 업데이트를 위해 기존 게시글 정보를 가져온다.
	 * @param simplePostId
	 * @param userId
	 * @return
	 */
	@Override
	public UpdatePostDto getFullPost(long simplePostId, long userId) {
		isLeaderValidation(simplePostId, userId);
		SimplePost findPost = simplePostRepository.findPostById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		return UpdatePostDto.of(findPost);
	}

	@Override
	@Transactional
	public void updatePost(long simplePostId, long userId, UpdatePostDto updatePostDto) {
		isLeaderValidation(simplePostId, userId);
		SimplePost findPost = simplePostRepository.findPostById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		findPost.update(updatePostDto.getTitle(), updatePostDto.getCategory(), updatePostDto.getRegion(),
			updatePostDto.getUserNum(), updatePostDto.getContent());
	}

	@Override
	@Transactional
	public void deletePost(long simplePostId, long userId) {
		isLeaderValidation(simplePostId, userId);
		simplePostRepository.deleteById(simplePostId);
	}

	@Override
	@Transactional
	public void closePost(long simplePostId, long userId) {
		isLeaderValidation(simplePostId, userId);
		SimplePost findPost = simplePostRepository.findPostById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		findPost.closePost();
	}

	private void isLeaderValidation(long simplePostId, long userId) {
		SimplePost findPost = simplePostRepository.findById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		if (!Objects.equals(findPost.getLeader().getId(), userId)) {
			throw new InvalidAccessException(ErrorMessage.NOT_LEADER);
		}
	}
}
