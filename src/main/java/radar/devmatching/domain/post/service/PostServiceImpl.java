package radar.devmatching.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.MainCommentResponse;
import radar.devmatching.domain.matchings.apply.service.ApplyService;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.service.MatchingService;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.post.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.service.dto.request.UpdatePostRequest;
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
		return simplePostRepository.findById(simplePostId).orElseThrow(() -> new RuntimeException());
	}

	@Override
	public PresentPostResponse getPostWithComment(long simplePostId) {
		SimplePost findPost = simplePostRepository.findPostById(simplePostId).orElseThrow(() -> new RuntimeException());
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
	public UpdatePostRequest getFullPost(long simplePostId, long leaderId) {
		isLeaderValidation(simplePostId, leaderId);
		SimplePost findPost = simplePostRepository.findPostById(simplePostId).orElseThrow(() -> new RuntimeException());
		return UpdatePostRequest.of(findPost);
	}

	//테스트
	@Override
	public void updatePost(long simplePostId, UpdatePostRequest updatePostRequest) {
		SimplePost findPost = simplePostRepository.findPostById(simplePostId).orElseThrow(() -> new RuntimeException());
		findPost.update(updatePostRequest.getTitle(), updatePostRequest.getCategory(), updatePostRequest.getRegion(),
			updatePostRequest.getUserNum(), updatePostRequest.getContent());
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
		List<SimplePost> myPosts = simplePostRepository.findMyPostsByLeaderId(userId);
		return myPosts.stream().map(SimplePostResponse::of).collect(Collectors.toList());
	}

	@Override
	public List<SimplePostResponse> getApplicationPosts(long userId) {
		List<SimplePost> applicationPosts = simplePostRepository.findApplicationPosts(userId);
		return applicationPosts.stream().map(SimplePostResponse::of).collect(Collectors.toList());
	}

	private void isLeaderValidation(long simplePostId, long leaderId) {
		SimplePost findPost = simplePostRepository.findById(simplePostId).orElseThrow(() -> new RuntimeException());
		Assert.isTrue(findPost.getLeader().getId() == leaderId, "you are not leader");
	}

}
