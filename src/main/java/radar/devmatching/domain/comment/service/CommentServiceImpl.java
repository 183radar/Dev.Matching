package radar.devmatching.domain.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.comment.repository.MainCommentRepository;
import radar.devmatching.domain.comment.repository.SubCommentRepository;
import radar.devmatching.domain.comment.service.dto.CommentDto;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.SimplePostRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final SimplePostRepository simplePostRepository;
	private final MainCommentRepository mainCommentRepository;
	private final SubCommentRepository subCommentRepository;

	@Override
	@Transactional
	public void createMainComment(long simplePostId, CommentDto commentDto) {
		SimplePost simplePost = simplePostRepository.findById(simplePostId).orElseThrow(() -> new RuntimeException());
		mainCommentRepository.save(commentDto.toMainCommentEntity(simplePost));
	}

}
