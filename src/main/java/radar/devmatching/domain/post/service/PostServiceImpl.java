package radar.devmatching.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.MainCommentResponse;
import radar.devmatching.domain.matchings.apply.service.ApplyService;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.service.MatchingService;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.exception.NotLeaderExceptionCustom;
import radar.devmatching.domain.post.exception.SimplePostNotFoundException;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.post.service.dto.UpdatePostDto;
import radar.devmatching.domain.post.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.service.dto.response.PresentPostResponse;
import radar.devmatching.domain.post.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final SimplePostRepository simplePostRepository;
	private final MatchingService matchingService;
	private final ApplyService applyService;
	private final CommentService commentService;

	@Override
	public SimplePost getSimplePostOnly(long simplePostId) {
		return simplePostRepository.findById(simplePostId).orElseThrow(SimplePostNotFoundException::new);
	}

	/**
	 * 게시글 화면 전체를 가져온다(게시글, 신청자 수, 댓글)
	 * @param simplePostId
	 * @return
	 */
	@Override
	public PresentPostResponse getPostWithComment(long simplePostId) {
		SimplePost findPost = simplePostRepository.findPostById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		int applyCount = applyService.getAcceptedApplyCount(simplePostId);
		List<MainCommentResponse> allComments = commentService.getAllComments(findPost.getFullPost().getId());

		return PresentPostResponse.of(findPost, applyCount, allComments);
	}

	/**
	 * 업데이트를 위해 기존 게시글 정보를 가져온다.
	 * @param simplePostId
	 * @param leaderId
	 * @return
	 */
	@Override
	public UpdatePostDto getFullPost(long simplePostId, long leaderId) {
		isLeaderValidation(simplePostId, leaderId);
		SimplePost findPost = simplePostRepository.findPostById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		return UpdatePostDto.of(findPost);
	}

	//테스트
	@Override
	@Transactional
	public void updatePost(long simplePostId, long leaderId, UpdatePostDto updatePostDto) {
		isLeaderValidation(simplePostId, leaderId);
		SimplePost findPost = simplePostRepository.findPostById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		findPost.update(updatePostDto.getTitle(), updatePostDto.getCategory(), updatePostDto.getRegion(),
			updatePostDto.getUserNum(), updatePostDto.getContent());
	}

	@Override
	@Transactional
	public void deletePost(long simplePostId, long leaderId) {
		isLeaderValidation(simplePostId, leaderId);
		simplePostRepository.deleteById(simplePostId);
	}

	@Override
	@Transactional
	public void closePost(long simplePostId, long leaderId) {
		isLeaderValidation(simplePostId, leaderId);
		SimplePost findPost = simplePostRepository.findPostById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		findPost.closePost();
	}

	@Override
	@Transactional
	public SimplePost createPost(CreatePostRequest createPostDto, User user) {
		Matching matching = matchingService.createMatching(user);
		SimplePost savedPost = simplePostRepository.save(createPostDto.toEntity(user, matching));
		return savedPost;
	}

	@Override
	public List<SimplePostResponse> getMyPosts(long userId) {
		List<SimplePost> myPosts = simplePostRepository.findMyPostsByLeaderIdOrderByCreateDateDesc(userId);
		return myPosts.stream().map(SimplePostResponse::of).collect(Collectors.toList());
	}

	@Override
	public List<SimplePostResponse> getApplicationPosts(long userId) {
		List<SimplePost> applicationPosts = simplePostRepository.findApplicationPosts(userId);
		return applicationPosts.stream().map(SimplePostResponse::of).collect(Collectors.toList());
	}

	private void isLeaderValidation(long simplePostId, long leaderId) {
		SimplePost findPost = simplePostRepository.findById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		if (findPost.getLeader().getId() == leaderId) {
			throw new NotLeaderExceptionCustom();
		}
	}

}
