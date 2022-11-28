package radar.devmatching.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.FullPostRepository;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.post.service.dto.CreatePostDto;
import radar.devmatching.domain.post.service.dto.PresentSimplePostDto;
import radar.devmatching.domain.post.service.dto.UpdatePostDto;
import radar.devmatching.domain.user.entity.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final SimplePostRepository simplePostRepository;
	private final FullPostRepository fullPostRepository;

	@Override
	public SimplePost getSimplePostOnly(long simplePostId) {
		return simplePostRepository.findById(simplePostId).orElseThrow(() -> new RuntimeException());
	}

	@Override
	public SimplePost getPost(long simplePostId) {
		return simplePostRepository.findPostById(simplePostId).orElseThrow(() -> new RuntimeException());
	}

	//테스트
	@Override
	public void updatePost(long simplePostId, UpdatePostDto updatePostDto) {
		SimplePost findPost = simplePostRepository.findPostById(simplePostId).orElseThrow(() -> new RuntimeException());
		findPost.update(updatePostDto.getTitle(), updatePostDto.getCategory(), updatePostDto.getRegion(),
			updatePostDto.getUserNum(), updatePostDto.getContent());
	}

	@Override
	@Transactional
	public SimplePost createPost(CreatePostDto createPostDto, User user) {
		return simplePostRepository.save(createPostDto.toEntity(user));
	}

	@Override
	public List<PresentSimplePostDto> getMyPosts(long userId) {
		List<SimplePost> myPosts = simplePostRepository.findMyPostsByLeaderId(userId);
		return myPosts.stream().map(PresentSimplePostDto::of).collect(Collectors.toList());
	}

	@Override
	public List<PresentSimplePostDto> getApplicationPosts(long userId) {
		List<SimplePost> applicationPosts = simplePostRepository.findApplicationPosts(userId);
		return applicationPosts.stream().map(PresentSimplePostDto::of).collect(Collectors.toList());
	}

}
