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
import radar.devmatching.domain.user.repository.UserRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

	private final UserRepository userRepository;
	private final ApplyRepository applyRepository;
	private final SimplePostService simplePostService;

	@Override
	@Transactional
	public Apply createApply(Long simplePostId, User authUser) {
		authUser = userRepository.findById(authUser.getId()).get();
		SimplePost simplePost = simplePostService.findById(simplePostId);

		applyRepository.findByApplySimplePostIdAndApplyUserId(simplePostId, authUser.getId())
			.ifPresent(apply -> {
				throw new AlreadyApplyException(ErrorMessage.ALREADY_APPLY);
			});

		Apply apply = Apply.builder().applyUser(authUser).applySimplePost(simplePost).build();

		applyRepository.save(apply);
		return apply;
	}

	@Override
	public List<ApplyResponse> getAllApplyList(User authUser) {
		//
		// return userTouch(authUser).getApplyList().stream()
		// 	.map(ApplyResponse::of)
		// 	.collect(Collectors.toList());

		return applyRepository.findAppliesByUserId(authUser.getId()).stream()
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

	@Override
	public Apply getApply(Long applyId) {
		return applyRepository.findById(applyId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.APPLY_NOT_FOUND));
	}

	private User userTouch(User authUser) {
		return userRepository.findById(authUser.getId()).orElseThrow(
			() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND)
		);
	}

}
