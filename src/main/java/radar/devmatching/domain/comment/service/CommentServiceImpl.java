package radar.devmatching.domain.comment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.repository.MainCommentRepository;
import radar.devmatching.domain.comment.repository.SubCommentRepository;
import radar.devmatching.domain.comment.service.dto.CreateCommentDto;
import radar.devmatching.domain.comment.service.dto.MainCommentResponse;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.exception.SimplePostNotFoundException;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.user.entity.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final SimplePostRepository simplePostRepository;
	private final MainCommentRepository mainCommentRepository;
	private final SubCommentRepository subCommentRepository;

	@Override
	@Transactional
	public void createMainComment(long simplePostId, User user, CreateCommentDto createCommentDto) {
		SimplePost simplePost = simplePostRepository.findById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		mainCommentRepository.save(createCommentDto.toMainCommentEntity(simplePost, user));
	}

	@Override
	public List<MainCommentResponse> getAllComments(long fullPostId) {
		List<MainComment> allComments = mainCommentRepository.getAllComments(fullPostId);
		return allComments.stream()
			.map(MainCommentResponse::of)
			.collect(Collectors.toList());
	}

}
