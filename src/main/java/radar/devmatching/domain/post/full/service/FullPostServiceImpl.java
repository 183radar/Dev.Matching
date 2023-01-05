package radar.devmatching.domain.post.full.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.response.MainCommentResponse;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.apply.service.ApplyService;
import radar.devmatching.domain.post.full.service.dto.UpdatePostDto;
import radar.devmatching.domain.post.full.service.dto.response.PresentPostResponse;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.UserService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FullPostServiceImpl implements FullPostService {

	private final UserService userService;
	private final SimplePostService simplePostService;
	private final ApplyRepository applyRepository;
	private final ApplyService applyService;
	private final CommentService commentService;

	/**
	 * 게시글 화면 전체를 가져온다(게시글, 신청자 수, 댓글)
	 *
	 * @param simplePostId
	 * @param loginUserId
	 * @return
	 */
	@Override
	@Transactional
	public PresentPostResponse getPostWithComment(long simplePostId, long loginUserId) {
		User user = userService.getUserEntity(loginUserId);
		SimplePost findPost = simplePostService.findPostById(simplePostId);

		// 나중에 새로고침 누르면 clickCount는 안 올라가도록 설정해도 좋을듯?
		findPost.plusClickCount();
		int applyCount = applyService.getAcceptedApplyCount(simplePostId);
		boolean isAppliedLoginUser = applyRepository.findByApplySimplePostIdAndApplyUserId(simplePostId, loginUserId)
			.isPresent();
		List<MainCommentResponse> allComments = commentService.getAllComments(findPost.getFullPost().getId());

		return PresentPostResponse.of(findPost, user, applyCount, isAppliedLoginUser, allComments);
	}

	/**
	 * 업데이트를 위해 기존 게시글 정보를 가져온다.
	 * @param simplePostId
	 * @param userId
	 * @return
	 */
	@Override
	public UpdatePostDto getUpdateFullPost(long simplePostId, long userId) {
		isLeaderValidation(simplePostId, userId);
		SimplePost findPost = simplePostService.findPostById(simplePostId);
		return UpdatePostDto.of(findPost);
	}

	@Override
	@Transactional
	public void updatePost(long simplePostId, long userId, UpdatePostDto updatePostDto) {
		isLeaderValidation(simplePostId, userId);
		SimplePost findPost = simplePostService.findPostById(simplePostId);
		findPost.update(updatePostDto.getTitle(), updatePostDto.getCategory(), updatePostDto.getRegion(),
			updatePostDto.getUserNum(), updatePostDto.getContent());
		log.info("Update Post: {}", updatePostDto);
	}

	@Override
	@Transactional
	public void deletePost(long simplePostId, long userId) {
		isLeaderValidation(simplePostId, userId);
		simplePostService.deleteById(simplePostId);
		log.info("Delete Post ID: {}", simplePostId);
	}

	@Override
	@Transactional
	public void closePost(long simplePostId, long userId) {
		isLeaderValidation(simplePostId, userId);
		SimplePost findPost = simplePostService.findPostById(simplePostId);
		findPost.closePost();
		log.info("Close Post: {}", findPost.getTitle());
	}

	private void isLeaderValidation(long simplePostId, long userId) {
		SimplePost findPost = simplePostService.findById(simplePostId);
		if (!Objects.equals(findPost.getLeader().getId(), userId)) {
			throw new InvalidAccessException(ErrorMessage.NOT_LEADER);
		}
	}
}
