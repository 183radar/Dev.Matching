package radar.devmatching.domain.matchings.apply.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.entity.ApplyState;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyResponse;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserService;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.user.entity.User;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplyLeaderServiceImpl implements ApplyLeaderService {

	private final ApplyRepository applyRepository;
	private final MatchingUserService matchingUserService;
	// SimplePostService로 변경 예정
	private final SimplePostRepository simplePostRepository;

	@Override
	@Transactional
	public void acceptApply(User authUser, Long applyId, Long simplePostId) {
		Apply apply = validatePermission(authUser, applyId, simplePostId).get();

		if (!Objects.equals(apply.getApplyState(), ApplyState.ACCEPTED)) {
			apply.acceptApply();

			Long userId = apply.getApplyUser().getId();
			Long matchingId = apply.getApplySimplePost().getMatching().getId();

			matchingUserService.createMatchingUser(userId, matchingId);
		}
	}

	@Override
	@Transactional
	public void denyApply(User authUser, Long applyId, Long simplePostId) {
		Apply apply = validatePermission(authUser, applyId, simplePostId).get();

		if (Objects.equals(apply.getApplyState(), ApplyState.ACCEPTED)) {
			Long matchingId = apply.getApplySimplePost().getMatching().getId();
			Long userId = authUser.getId();

			matchingUserService.deleteMatchingUser(matchingId, userId);
		}

		apply.denyApply();
	}

	@Override
	public List<ApplyResponse> getAllApplyList(User authUser, Long simplePostId) {
		validatePermission(authUser, null, simplePostId);

		return applyRepository.findAllByApplySimplePostId(simplePostId).stream()
			.map(ApplyResponse::of)
			.collect(Collectors.toList());
	}

	/**
	 * simplePost에 접근하는 사용자가 리더인지 확인
	 * applyId가 null이 아니라면 apply가 simplePost에 있는지 확인
	 * @param authUser
	 * @param applyId
	 * @param simplePostId
	 */
	private Optional<Apply> validatePermission(User authUser, Long applyId, Long simplePostId) {
		SimplePost simplePost = simplePostRepository.findById(simplePostId)
			.orElseThrow(() -> new RuntimeException("not exist simplePost entity"));

		User leader = simplePost.getUser();
		if (!Objects.equals(leader.getId(), authUser.getId())) {
			throw new RuntimeException("Invalid Access");
		}

		if (Objects.nonNull(applyId)) {
			Apply apply = applyRepository.findById(applyId)
				.orElseThrow(() -> new RuntimeException("not exist apply entity"));

			if (!Objects.equals(apply.getApplySimplePost().getId(), simplePost.getId())) {
				throw new RuntimeException("Invalid Access");
			}
			return Optional.of(apply);
		}
		return Optional.empty();
	}
}
