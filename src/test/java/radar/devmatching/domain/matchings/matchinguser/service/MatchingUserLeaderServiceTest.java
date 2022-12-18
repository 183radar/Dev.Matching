package radar.devmatching.domain.matchings.matchinguser.service;

import static org.mockito.BDDMockito.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.repository.MatchingUserRepository;
import radar.devmatching.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchingUserLeaderService의")
class MatchingUserLeaderServiceTest {

	private static final Long TEST_USER_ID = 1L;

	@Mock
	MatchingUserRepository matchingUserRepository;

	MatchingUserLeaderService matchingUserLeaderService;

	@BeforeEach
	void setUp() {
		matchingUserLeaderService = new MatchingUserLeaderServiceImpl(matchingUserRepository);
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

	private User createUser() throws NoSuchFieldException, IllegalAccessException {
		User user = basicUser();

		Class<User> userClass = User.class;
		Field userId = userClass.getDeclaredField("id");
		userId.setAccessible(true);
		userId.set(user, TEST_USER_ID);

		return user;
	}

	@Test
	@DisplayName("createMatchingUserLeader 메서드는 user 엔티티와 matching 엔티티 입력 받으면 leader 상태를 담은 matchingUser 엔티티를 생성한다.")
	public void createMatchingUserLeaderMethod() throws NoSuchFieldException, IllegalAccessException {
		//given
		User user = createUser();
		Matching matching = Matching.builder().build();
		//when
		MatchingUser matchingUser = matchingUserLeaderService.createMatchingUserLeader(user, matching);
		//then
		verify(matchingUserRepository, times(1)).save(matchingUser);
	}
}