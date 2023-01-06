package radar.devmatching.domain.matchings.apply.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.entity.ApplyState;
import radar.devmatching.domain.matchings.apply.exception.AlreadyApplyException;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyResponse;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.UserService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

	private final ApplyRepository applyRepository;
	private final SimplePostService simplePostService;
	private final UserService userService;

	@Override
	@Transactional
	public Apply createApply(Long simplePostId, Long userId) {
		User user = userService.findById(userId);
		SimplePost simplePost = simplePostService.findById(simplePostId);

		// TODO : Querydsl 로 이름 간략하게 바꾸기
		applyRepository.findByApplySimplePostIdAndApplyUserId(simplePostId, userId)
			.ifPresent(apply -> {
				throw new AlreadyApplyException(ErrorMessage.ALREADY_APPLY);
			});

		Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();

		applyRepository.save(apply);
		return apply;
	}

	@Override
	public List<ApplyResponse> getAllApplyList(Long userId) {

		return applyRepository.findAppliesByUserId(userId).stream()
			.map(ApplyResponse::of)
			.collect(Collectors.toList());
	}

	// TODO : 리펙터링하기
	@Override
	public int getAcceptedApplyCount(Long simplePostId) {
		long count = applyRepository.findAllByApplySimplePostId(simplePostId).stream()
			.filter(apply -> Objects.equals(apply.getApplyState(), ApplyState.ACCEPTED))
			.count();
		return Math.toIntExact(count);
	}

	@Override
	public Apply findById(Long applyId) {
		return applyRepository.findById(applyId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.APPLY_NOT_FOUND));
	}

}
