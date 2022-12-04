package radar.devmatching.domain.matchings.matchinguser.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUserRole;
import radar.devmatching.domain.matchings.matchinguser.repository.MatchingUserRepository;
import radar.devmatching.domain.user.entity.User;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchingUserLeaderServiceImpl implements MatchingUserLeaderService {

	private final MatchingUserRepository matchingUserRepository;

	@Transactional
	public MatchingUser createMatchingUserLeader(User leader, Matching matching) {
		// 검증이 들어갔다는걸 전재로함
		MatchingUser matchingUser = new MatchingUser(MatchingUserRole.LEADER, leader, matching);
		matchingUserRepository.save(matchingUser);
		return matchingUser;
	}

}
