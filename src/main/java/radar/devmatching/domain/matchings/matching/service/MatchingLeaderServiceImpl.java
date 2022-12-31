package radar.devmatching.domain.matchings.matching.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.repository.MatchingRepository;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserLeaderService;
import radar.devmatching.domain.user.entity.User;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchingLeaderServiceImpl implements MatchingLeaderService {

	private final MatchingRepository matchingRepository;
	private final MatchingUserLeaderService matchingUserLeaderService;

	@Override
	@Transactional
	public Matching createMatching(User authUser) {
		Matching matching = Matching.builder().build();
		matchingRepository.save(matching);

		matchingUserLeaderService.createMatchingUserLeader(authUser, matching);
		return matching;
	}

}
