package radar.devmatching.domain.post.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.post.repository.FullPostRepository;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.post.service.dto.CreatePostDto;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final SimplePostRepository simplePostRepository;
	private final FullPostRepository fullPostRepository;

	@Override
	public void createPost(CreatePostDto createPostDto) {
		//cascade로 fullpost까지 저장되는지 확인해야됨
		simplePostRepository.save(createPostDto.toEntity());
	}
}
