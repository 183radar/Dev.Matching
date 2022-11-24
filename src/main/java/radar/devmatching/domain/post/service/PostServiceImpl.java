package radar.devmatching.domain.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.FullPostRepository;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.post.service.dto.CreatePostDto;
import radar.devmatching.domain.user.entity.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final SimplePostRepository simplePostRepository;
	private final FullPostRepository fullPostRepository;

	@Override
	@Transactional
	public SimplePost createPost(CreatePostDto createPostDto, User user) {
		return simplePostRepository.save(createPostDto.toEntity(user));
	}

	@Override
	public List<SimplePost> getMyPosts(long userId) {
		return simplePostRepository.findMyPostByUserId(userId);
	}

	@Override
	public List<SimplePost> findApplicationPost(long userId) {
		return simplePostRepository.findApplicationPosts(userId);
	}

}
