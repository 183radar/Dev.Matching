package radar.devmatching.domain.post.simple.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.service.MatchingService;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.exception.SimplePostNotFoundException;
import radar.devmatching.domain.post.simple.repository.SimplePostRepository;
import radar.devmatching.domain.post.simple.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SimplePostServiceImpl implements SimplePostService {

	private final SimplePostRepository simplePostRepository;
	private final MatchingService matchingService;

	@Override
	@Transactional
	public long createPost(CreatePostRequest createPostRequest, User user) {
		Matching matching = matchingService.createMatching(user);
		SimplePost savedPost = simplePostRepository.save(createPostRequest.toEntity(user, matching));
		return savedPost.getId();
	}

	@Override
	public SimplePost getSimplePostOnly(long simplePostId) {
		return simplePostRepository.findById(simplePostId).orElseThrow(SimplePostNotFoundException::new);
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

}
