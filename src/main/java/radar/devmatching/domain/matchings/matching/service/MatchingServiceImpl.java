package radar.devmatching.domain.matchings.matching.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.repository.MatchingRepository;
import radar.devmatching.domain.matchings.matching.service.dto.MatchingUpdate;
import radar.devmatching.domain.matchings.matching.service.dto.response.MatchingResponse;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserService;
import radar.devmatching.domain.user.entity.User;

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

	@Override
	public MatchingResponse getMatchingInfo(Long matchingId, User user) {
		Matching matching = getMatching(matchingId);
		log.info("access matching info={}", matching);
		return MatchingResponse.of(matching, user);
	}

	@Override
	public MatchingUpdate getMatchingUpdateData(Long matchingId) {
		Matching matching = getMatching(matchingId);

		return MatchingUpdate.of(matching);
	}

	@Override
	@Transactional
	public void updateMatching(Long matchingId, MatchingUpdate matchingUpdate) {
		log.info("update request={}", matchingUpdate);

		Matching matching = getMatching(matchingId);
		if (!Objects.equals(matchingId, matchingUpdate.getMatchingId())) {
			throw new RuntimeException();
		}

		matching.update(matchingUpdate.getMatchingTitle(), matchingUpdate.getMatchingInfo());
	}

	private Matching getMatching(Long matchingId) {
		return matchingRepository.findById(matchingId)
			.orElseThrow(() -> new RuntimeException());
	}
}
