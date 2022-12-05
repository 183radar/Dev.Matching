package radar.devmatching.domain.matchings.matchinguser.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.exception.AlreadyJoinMatchingUserException;
import radar.devmatching.domain.matchings.matchinguser.repository.MatchingUserRepository;
import radar.devmatching.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchingUserService의 ")
class MatchingUserServiceTest {

	private static final Long TEST_USER_ID = 1L;
	private static final Long TEST_MATCHING_ID = 2L;

	@Mock
	MatchingUserRepository matchingUserRepository;

	MatchingUserService matchingUserService;

	@BeforeEach
	void setUp() {
		matchingUserService = new MatchingUserServiceImpl(matchingUserRepository);
	}

	private User createUser() {
		return User.builder()
			.id(TEST_USER_ID)
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("schoolName")
			.githubUrl("githubUrl")
			.introduce("introduce")
			.build();
	}

	@Nested
	@DisplayName("createMatchingUser 메서드에서")
	class CreateMatchingUserMethod {

		@Test
		@DisplayName("정상적으로 matchingUser 엔티티가 저장된다.")
		public void saveMatchingUserWithoutException() {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			given(matchingUserRepository.existsByMatchingIdAndUserId(any(), any())).willReturn(false);

			//when
			MatchingUser matchingUser = matchingUserService.createMatchingUser(matching, user);
			//then
			verify(matchingUserRepository, times(1)).save(matchingUser);
		}

		@Test
		@DisplayName("해당 User가 이미 저장되어 있으면 예외를 던진다.")
		public void throwAlreadyJoinMatchingUserException() {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			given(matchingUserRepository.existsByMatchingIdAndUserId(any(), any())).willReturn(true);
			//when
			//then
			assertThatThrownBy(() -> matchingUserService.createMatchingUser(matching, user))
				.isInstanceOf(AlreadyJoinMatchingUserException.class);
		}
	}
}