package radar.devmatching.domain.matchings.matching.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.repository.MatchingRepository;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserLeaderService;
import radar.devmatching.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchingLeaderService의")
public class MatchingLeaderServiceTest {

	private static final Long USER_ID = 1L;

	@Mock
	MatchingRepository matchingRepository;
	@Mock
	MatchingUserLeaderService matchingUserLeaderService;

	MatchingLeaderService matchingLeaderService;

	@BeforeEach
	void setUp() {
		matchingLeaderService = new MatchingLeaderServiceImpl(matchingRepository, matchingUserLeaderService);
	}

	private User basicUser() {
		return User.builder()
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("schoolName")
			.githubUrl("githubUrl")
			.introduce("introduce")
			.build();
	}

	private User createUser() {
		User user = basicUser();

		ReflectionTestUtils.setField(user, "id", USER_ID);

		return user;
	}

	@Test
	@DisplayName("createMatching 메서드는 요청 user를 방장으로하는 Matching을 만든다.")
	void createMatching() {
		//given
		User user = createUser();
		//when
		matchingLeaderService.createMatching(user);
		//then
		verify(matchingRepository, times(1)).save(any(Matching.class));
		verify(matchingUserLeaderService, times(1)).createMatchingUserLeader(eq(user), any(Matching.class));
	}

}
