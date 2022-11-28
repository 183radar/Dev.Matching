package radar.devmatching.domain.matchings.matchinguser.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.repository.MatchingRepository;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUserRole;
import radar.devmatching.domain.matchings.matchinguser.repository.MatchingUserRepository;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchingUserServiceImpl implements MatchingUserService {

	private final MatchingUserRepository matchingUserRepository;

	private final UserRepository userRepository;
	private final MatchingRepository matchingRepository;

	@Override
	@Transactional
	public void createMatchingUser(Long userId, Long matchingId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException());
		Matching matching = matchingRepository.findById(matchingId).orElseThrow(() -> new RuntimeException());

		MatchingUser matchingUser = new MatchingUser(MatchingUserRole.USER, user, matching);
		matchingUserRepository.save(matchingUser);
	}

	@Override
	@Transactional
	public void deleteMatchingUser(Long matchingId, Long userId) {
		matchingUserRepository.deleteByMatchingIdAndUserId(matchingId, userId);
	}
}
