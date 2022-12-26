package radar.devmatching.domain.comment.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.entity.SubComment;
import radar.devmatching.domain.comment.repository.MainCommentRepository;
import radar.devmatching.domain.comment.repository.SubCommentRepository;
import radar.devmatching.domain.comment.service.dto.UpdateCommentDto;
import radar.devmatching.domain.comment.service.dto.request.CreateCommentRequest;
import radar.devmatching.domain.comment.service.dto.response.MainCommentResponse;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.exception.SimplePostNotFoundException;
import radar.devmatching.domain.post.simple.repository.SimplePostRepository;
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
	public void createMainComment(long simplePostId, User loginUser, CreateCommentRequest createCommentRequest) {
		SimplePost simplePost = simplePostRepository.findById(simplePostId)
			.orElseThrow(SimplePostNotFoundException::new);
		mainCommentRepository.save(createCommentRequest.toMainCommentEntity(simplePost, loginUser));
	}

	@Override
	@Transactional
	public long createSubComment(long mainCommentId, User loginUser, CreateCommentRequest createCommentRequest) {
		MainComment mainComment = mainCommentRepository.findMainCommentById(mainCommentId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND));
		SubComment subComment = subCommentRepository.save(
			createCommentRequest.toSubCommentEntity(mainComment, loginUser));
		return subCommentRepository.findBySimplePostIdAsSubCommentId(subComment.getId());
	}

	@Override
	public List<MainCommentResponse> getAllComments(long fullPostId) {
		List<MainComment> allComments = mainCommentRepository.getAllComments(fullPostId);
		return allComments.stream()
			.map(MainCommentResponse::of)
			.collect(Collectors.toList());
	}

	@Override
	public void mainCommentExistById(long mainCommentId) {
		if (!mainCommentRepository.existsById(mainCommentId)) {
			throw new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND);
		}
	}

	@Override
	public UpdateCommentDto getMainCommentOnly(long mainCommentId) {
		MainComment mainComment = mainCommentRepository.findMainCommentById(mainCommentId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND));
		return UpdateCommentDto.of(mainComment, UpdateCommentDto.CommentType.MAIN);
	}

	@Override
	public UpdateCommentDto getSubCommentOnly(long subCommentId) {
		SubComment subComment = subCommentRepository.findSubCommentById(subCommentId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SUB_COMMENT_NOT_FOUND));
		return UpdateCommentDto.of(subComment, UpdateCommentDto.CommentType.SUB);
	}

	@Override
	@Transactional
	public long updateMainComment(long mainCommentId, UpdateCommentDto updateCommentDto, User loginUser) {
		MainComment mainComment = validationMainCommentOwner(mainCommentId, loginUser);
		mainComment.update(updateCommentDto.getContent());
		Long simplePostId = mainCommentRepository.findBySimplePostIdAsMainCommentId(mainCommentId);
		if (Objects.isNull(simplePostId)) {
			throw new EntityNotFoundException(ErrorMessage.SIMPLE_POST_NOT_FOUND);
		}
		return simplePostId;
	}

	@Override
	@Transactional
	public long updateSubComment(long subCommentId, UpdateCommentDto updateCommentDto, User loginUser) {
		SubComment subComment = validationSubCommentOwner(subCommentId, loginUser);
		subComment.update(updateCommentDto.getContent());
		Long simplePostId = subCommentRepository.findBySimplePostIdAsSubCommentId(subCommentId);
		if (Objects.isNull(simplePostId)) {
			throw new EntityNotFoundException(ErrorMessage.SIMPLE_POST_NOT_FOUND);
		}
		return simplePostId;
	}

	@Override
	@Transactional
	public void deleteMainComment(long mainCommentId, User authUser) {
		MainComment mainComment = validationMainCommentOwner(mainCommentId, authUser);
		mainCommentRepository.delete(mainComment);
	}

	@Override
	@Transactional
	public void deleteSubComment(long subCommentId, User loginUser) {
		SubComment subComment = validationSubCommentOwner(subCommentId, loginUser);
		subCommentRepository.delete(subComment);
	}

	private MainComment validationMainCommentOwner(long mainCommentId, User loginUser) {
		MainComment mainComment = mainCommentRepository.findMainCommentById(mainCommentId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND));
		if (!Objects.equals(mainComment.getComment().getUser().getId(), loginUser.getId())) {
			throw new InvalidAccessException(ErrorMessage.NOT_COMMENT_OWNER);
		}
		return mainComment;
	}

	private SubComment validationSubCommentOwner(long subCommentId, User loginUser) {
		SubComment subComment = subCommentRepository.findSubCommentById(subCommentId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SUB_COMMENT_NOT_FOUND));
		if (!Objects.equals(subComment.getComment().getUser().getId(), loginUser.getId())) {
			throw new InvalidAccessException(ErrorMessage.NOT_COMMENT_OWNER);
		}
		return subComment;
	}
}
