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
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final UserRepository userRepository;
	private final SimplePostService simplePostService;
	private final MainCommentRepository mainCommentRepository;
	private final SubCommentRepository subCommentRepository;

	@Override
	@Transactional
	public void createMainComment(long simplePostId, long loginUserId, CreateCommentRequest createCommentRequest) {
		User referenceLoginUser = userRepository.getReferenceById(loginUserId);
		SimplePost simplePost = simplePostService.findById(simplePostId);
		mainCommentRepository.save(createCommentRequest.toMainCommentEntity(simplePost, referenceLoginUser));
	}

	@Override
	@Transactional
	public long createSubComment(long mainCommentId, long loginUserId, CreateCommentRequest createCommentRequest) {
		User referenceLoginUser = userRepository.getReferenceById(loginUserId);
		MainComment mainComment = mainCommentRepository.findMainCommentById(mainCommentId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND));
		SubComment subComment = subCommentRepository.save(
			createCommentRequest.toSubCommentEntity(mainComment, referenceLoginUser));
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
		return UpdateCommentDto.of(mainCommentId, mainComment, UpdateCommentDto.CommentType.MAIN);
	}

	@Override
	public UpdateCommentDto getSubCommentOnly(long subCommentId) {
		SubComment subComment = subCommentRepository.findSubCommentById(subCommentId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SUB_COMMENT_NOT_FOUND));
		return UpdateCommentDto.of(subCommentId, subComment, UpdateCommentDto.CommentType.SUB);
	}

	@Override
	@Transactional
	public long updateMainComment(long mainCommentId, UpdateCommentDto updateCommentDto, long loginUserId) {
		MainComment mainComment = validationMainCommentOwner(mainCommentId, loginUserId);
		mainComment.update(updateCommentDto.getContent());
		Long simplePostId = mainCommentRepository.findBySimplePostIdAsMainCommentId(mainCommentId);
		if (Objects.isNull(simplePostId)) {
			throw new EntityNotFoundException(ErrorMessage.SIMPLE_POST_NOT_FOUND);
		}
		return simplePostId;
	}

	@Override
	@Transactional
	public long updateSubComment(long subCommentId, UpdateCommentDto updateCommentDto, long loginUserId) {
		SubComment subComment = validationSubCommentOwner(subCommentId, loginUserId);
		subComment.update(updateCommentDto.getContent());
		Long simplePostId = subCommentRepository.findBySimplePostIdAsSubCommentId(subCommentId);
		if (Objects.isNull(simplePostId)) {
			throw new EntityNotFoundException(ErrorMessage.SIMPLE_POST_NOT_FOUND);
		}
		return simplePostId;
	}

	@Override
	@Transactional
	public Long deleteMainComment(long mainCommentId, long loginUserId) {
		MainComment mainComment = validationMainCommentOwner(mainCommentId, loginUserId);
		Long simplePostId = mainCommentRepository.findBySimplePostIdAsMainCommentId(mainCommentId);
		if (Objects.isNull(simplePostId)) {
			throw new EntityNotFoundException(ErrorMessage.SIMPLE_POST_NOT_FOUND);
		}
		mainCommentRepository.delete(mainComment);
		return simplePostId;
	}

	@Override
	@Transactional
	public Long deleteSubComment(long subCommentId, long loginUserId) {
		SubComment subComment = validationSubCommentOwner(subCommentId, loginUserId);
		Long simplePostId = subCommentRepository.findBySimplePostIdAsSubCommentId(subCommentId);
		if (Objects.isNull(simplePostId)) {
			throw new EntityNotFoundException(ErrorMessage.SIMPLE_POST_NOT_FOUND);
		}
		subCommentRepository.delete(subComment);
		return simplePostId;
	}

	private MainComment validationMainCommentOwner(long mainCommentId, long loginUserId) {
		MainComment mainComment = mainCommentRepository.findMainCommentById(mainCommentId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND));
		if (!Objects.equals(mainComment.getComment().getUser().getId(), loginUserId)) {
			throw new InvalidAccessException(ErrorMessage.NOT_COMMENT_OWNER);
		}
		return mainComment;
	}

	private SubComment validationSubCommentOwner(long subCommentId, long loginUserId) {
		SubComment subComment = subCommentRepository.findSubCommentById(subCommentId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SUB_COMMENT_NOT_FOUND));
		if (!Objects.equals(subComment.getComment().getUser().getId(), loginUserId)) {
			throw new InvalidAccessException(ErrorMessage.NOT_COMMENT_OWNER);
		}
		return subComment;
	}
}
