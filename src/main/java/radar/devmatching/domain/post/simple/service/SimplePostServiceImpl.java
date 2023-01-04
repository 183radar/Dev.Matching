package radar.devmatching.domain.post.simple.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.exception.InvalidParamException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.service.MatchingLeaderService;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.PostState;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.exception.SimplePostNotFoundException;
import radar.devmatching.domain.post.simple.repository.SimplePostRepository;
import radar.devmatching.domain.post.simple.service.dto.MainPostDto;
import radar.devmatching.domain.post.simple.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.UserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SimplePostServiceImpl implements SimplePostService {

	private final UserService userService;
	private final SimplePostRepository simplePostRepository;
	private final MatchingLeaderService matchingLeaderService;

	/**
	 * 단순 조회에 예외 처리를 한 곳에서 처리하기 위해 만든 메서드
	 * 나중에 순환참조같은 문제 발생할 것 같으면 서비스 계층 세분화해서 나누어도 좋을 듯
	 */

	@Override
	public SimplePost findById(long simplePostId) {
		return simplePostRepository.findById(simplePostId).orElseThrow(SimplePostNotFoundException::new);
	}

	@Override
	public SimplePost findPostById(long simplePostId) {
		return simplePostRepository.findPostById(simplePostId).orElseThrow(SimplePostNotFoundException::new);
	}

	@Override
	public void deleteById(long simplePostId) {
		simplePostRepository.deleteById(simplePostId);
	}

	@Override
	@Transactional
	public long createPost(CreatePostRequest createPostRequest, long loginUserId) {
		User loginUser = userService.getUserEntity(loginUserId);
		Matching matching = matchingLeaderService.createMatching(loginUser);
		SimplePost savedPost = simplePostRepository.save(createPostRequest.toEntity(loginUser, matching));
		return savedPost.getId();
	}

	@Override
	public MainPostDto getMainPostDto(long loginUserId, String postCategoryParam) {
		User loginUser = userService.getUserEntity(loginUserId);
		List<SimplePost> simplePosts = getSimplePostsWhichCategoryEq(postCategoryParam);
		return MainPostDto.of(loginUser.getNickName(), null, simplePosts);
	}

	@Override
	public MainPostDto searchSimplePost(long loginUserId, String postCategory, MainPostDto mainPostDto) {
		User loginUser = userService.getUserEntity(loginUserId);
		List<SimplePost> simplePosts = simplePostRepository.findRecruitingPostBySearchCondition(postCategory,
			mainPostDto);
		return MainPostDto.of(loginUser.getNickName(), mainPostDto.getRegion(), simplePosts);
	}

	@Override
	public List<SimplePostResponse> getMyPosts(long loginUserId) {
		List<SimplePost> myPosts = simplePostRepository.findMyPostsByLeaderIdOrderByCreateDateDesc(loginUserId);
		return myPosts.stream().map(SimplePostResponse::of).collect(Collectors.toList());
	}

	@Override
	public List<SimplePostResponse> getApplicationPosts(long loginUserId) {
		List<SimplePost> applicationPosts = simplePostRepository.findApplicationPosts(loginUserId);
		return applicationPosts.stream().map(SimplePostResponse::of).collect(Collectors.toList());
	}

	private List<SimplePost> getSimplePostsWhichCategoryEq(String postCategory) {
		try {
			return simplePostRepository.findByCategoryAndPostStateOrderByCreateDateDesc(
				PostCategory.valueOf(postCategory), PostState.RECRUITING);
		} catch (IllegalArgumentException e) {
			if (postCategory.equals("ALL")) {
				return simplePostRepository.findByPostStateOrderByCreateDateDesc(PostState.RECRUITING);
			}
			throw new InvalidParamException(ErrorMessage.INVALID_POST_CATEGORY, e);
		}
	}
}
