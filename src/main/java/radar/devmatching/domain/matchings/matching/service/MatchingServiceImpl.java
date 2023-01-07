package radar.devmatching.domain.matchings.matching.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.repository.MatchingRepository;
import radar.devmatching.domain.matchings.matching.service.dto.MatchingInfo;
import radar.devmatching.domain.matchings.matching.service.dto.MatchingUpdate;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserService;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.UserService;

/**
 * TODO : 접근 검증 추가해야함
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingServiceImpl implements MatchingService {

	private final MatchingRepository matchingRepository;
	private final MatchingUserService matchingUserService;
	private final UserService userService;

	@Override
	public MatchingInfo getMatchingInfo(Long matchingId, Long userId) {
		MatchingUser matchingUser = matchingUserService.findByMatchingIdAndUserId(matchingId, userId);

		Matching matching = matchingUser.getMatching();

		User user = userService.findById(userId);
		log.info("access matching info={}", matching);
		return MatchingInfo.of(matchingUser);
	}

	@Override
	public MatchingUpdate getMatchingUpdateData(Long matchingId) {
		Matching matching = findById(matchingId);

		return MatchingUpdate.of(matching);
	}

	@Override
	@Transactional
	public void updateMatching(Long matchingId, MatchingUpdate matchingUpdate) {
		log.info("update request={}", matchingUpdate);

		Matching matching = findById(matchingId);
		if (!Objects.equals(matchingId, matchingUpdate.getMatchingId())) {
			throw new InvalidAccessException(ErrorMessage.INVALID_ACCESS);
		}

		matching.update(matchingUpdate.getMatchingTitle(), matchingUpdate.getMatchingInfo());
	}

	@Override
	public Matching findById(Long matchingId) {
		return matchingRepository.findById(matchingId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MATCHING_NOT_FOUND));
	}
}
