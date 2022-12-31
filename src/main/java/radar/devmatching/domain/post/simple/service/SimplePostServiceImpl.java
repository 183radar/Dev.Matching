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
import radar.devmatching.domain.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SimplePostServiceImpl implements SimplePostService {

	private final UserRepository userRepository;
	private final SimplePostRepository simplePostRepository;
	private final MatchingLeaderService matchingLeaderService;

	@Override
	@Transactional
	public long createPost(CreatePostRequest createPostRequest, User user) {
		user = userRepository.findById(user.getId()).get();
		Matching matching = matchingLeaderService.createMatching(user);
		SimplePost savedPost = simplePostRepository.save(createPostRequest.toEntity(user, matching));
		return savedPost.getId();
	}

	@Override
	public SimplePost getSimplePostOnly(long simplePostId) {
		return simplePostRepository.findById(simplePostId).orElseThrow(SimplePostNotFoundException::new);
	}

	@Override
	public MainPostDto getMainPostDto(User loginUser, String postCategoryParam) {
		List<SimplePost> simplePosts = getSimplePostsWhichCategoryEq(postCategoryParam);
		return MainPostDto.of(loginUser.getNickName(), null, simplePosts);
	}

	@Override
	public MainPostDto searchSimplePost(User loginUser, String postCategory, MainPostDto mainPostDto) {
		List<SimplePost> simplePosts = simplePostRepository.findRecruitingPostBySearchCondition(postCategory,
			mainPostDto);
		return MainPostDto.of(loginUser.getNickName(), mainPostDto.getRegion(), simplePosts);
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
