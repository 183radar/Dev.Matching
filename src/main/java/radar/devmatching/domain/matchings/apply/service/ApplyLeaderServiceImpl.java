package radar.devmatching.domain.matchings.apply.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.entity.ApplyState;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyLeaderResponse;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserService;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.user.entity.User;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplyLeaderServiceImpl implements ApplyLeaderService {

	private final ApplyRepository applyRepository;
	private final ApplyService applyService;
	private final MatchingUserService matchingUserService;
	private final SimplePostService simplePostService;

	@Override
	@Transactional
	public void acceptApply(Long userId, Long applyId, Long simplePostId) {
		Apply apply = validatePermission(userId, applyId, simplePostId);

		if (!Objects.equals(apply.getApplyState(), ApplyState.ACCEPTED)) {
			apply.acceptApply();

			User applyUser = apply.getApplyUser();
			Matching matching = apply.getApplySimplePost().getMatching();

			matchingUserService.createMatchingUser(matching, applyUser);
		}
	}

	@Override
	@Transactional
	public void denyApply(Long userId, Long applyId, Long simplePostId) {
		Apply apply = validatePermission(userId, applyId, simplePostId);

		if (Objects.equals(apply.getApplyState(), ApplyState.ACCEPTED)) {
			Long matchingId = apply.getApplySimplePost().getMatching().getId();

			matchingUserService.deleteMatchingUser(matchingId, userId);
		}

		apply.denyApply();
	}

	@Override
	public List<ApplyLeaderResponse> getAllApplyList(Long userId, Long simplePostId) {
		validatePermission(userId, null, simplePostId);

		return applyRepository.findAllByApplySimplePostId(simplePostId).stream()
			.map(ApplyLeaderResponse::of)
			.collect(Collectors.toList());
	}

	/**
	 * simplePost에 접근하는 사용자가 리더인지 확인
	 * applyId가 null이 아니라면 apply가 simplePost에 있는지 확인
	 */
	private Apply validatePermission(Long userId, Long applyId, Long simplePostId) {
		SimplePost simplePost = simplePostService.findById(simplePostId);

		User leader = simplePost.getLeader();
		if (!Objects.equals(leader.getId(), userId)) {
			throw new InvalidAccessException(ErrorMessage.INVALID_ACCESS);
		}

		if (Objects.nonNull(applyId)) {
			Apply apply = applyService.findById(applyId);

			if (!Objects.equals(apply.getApplySimplePost().getId(), simplePostId)) {
				throw new InvalidAccessException(ErrorMessage.INVALID_ACCESS);
			}
			return apply;
		}
		return null;
	}
}
