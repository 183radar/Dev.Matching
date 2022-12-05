package radar.devmatching.domain.matchings.apply.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.entity.ApplyState;
import radar.devmatching.domain.matchings.apply.exception.AlreadyApplyException;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyResponse;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.user.entity.User;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

	private final ApplyRepository applyRepository;
	private final SimplePostRepository simplePostRepository;

	@Override
	@Transactional
	public Apply createApply(Long simplePostId, User authUser) {
		SimplePost simplePost = simplePostRepository.findById(simplePostId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SIMPLE_POST_NOT_FOUND));

		applyRepository.findByApplySimplePostIdAndApplyUserId(simplePostId, authUser.getId())
			.ifPresent(apply -> {
				throw new AlreadyApplyException(ErrorMessage.ALREADY_APPLY);
			});

		Apply apply = Apply.builder().applyUser(authUser).applySimplePost(simplePost).build();

		applyRepository.save(apply);
		return apply;
	}

	@Override
	public List<ApplyResponse> getAllApplyList(User authUser, Long userId) {
		if (!Objects.equals(authUser.getId(), userId)) {
			throw new InvalidAccessException(ErrorMessage.INVALID_ACCESS);
		}

		return authUser.getApplyList().stream()
			.map(ApplyResponse::of)
			.collect(Collectors.toList());
	}

	@Override
	public int getAcceptedApplyCount(Long simplePostId) {
		long count = applyRepository.findAllByApplySimplePostId(simplePostId).stream()
			.filter(apply -> Objects.equals(apply.getApplyState(), ApplyState.ACCEPTED))
			.count();
		return Math.toIntExact(count);
	}

}
