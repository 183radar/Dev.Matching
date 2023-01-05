package radar.devmatching.domain.matchings.matchinguser.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUserRole;
import radar.devmatching.domain.matchings.matchinguser.exception.AlreadyJoinMatchingUserException;
import radar.devmatching.domain.matchings.matchinguser.repository.MatchingUserRepository;
import radar.devmatching.domain.matchings.matchinguser.service.dto.response.MatchingUserResponse;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("MatchingUserService의 ")
class MatchingUserServiceTest {

	private static final Long TEST_USER_ID = 1L;
	private static final Long MATCHING_ID = 2L;

	@Mock
	MatchingUserRepository matchingUserRepository;

	MatchingUserService matchingUserService;

	@BeforeEach
	void setUp() {
		matchingUserService = new MatchingUserServiceImpl(matchingUserRepository);
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

		ReflectionTestUtils.setField(user, "id", TEST_USER_ID);

		return user;
	}

	private Matching createMatching() {

		Matching matching = Matching.builder()
			.build();

		ReflectionTestUtils.setField(matching, "id", MATCHING_ID);

		SimplePost simplePost = SimplePost.builder()
			.leader(createUser())
			.fullPost(FullPost.builder().content("test").build())
			.title("test")
			.matching(matching)
			.build();

		return matching;
	}

	@Test
	@DisplayName("getMatchingUserList 메서드에서 userId에 해당하는 matchingUser 리스트를 반환한다.")
	void getMatchingUserList() {
		//given
		User user = createUser();
		Matching matching = createMatching();
		MatchingUser matchingUser = MatchingUser.builder()
			.user(user)
			.matching(matching)
			.matchingUserRole(MatchingUserRole.USER)
			.build();
		List<MatchingUser> list = new ArrayList<>();
		list.add(matchingUser);
		given(matchingUserRepository.findMatchingUserList(user.getId())).willReturn(list);
		//when
		List<MatchingUserResponse> matchingUserList = matchingUserService.getMatchingUserList(user.getId());
		//then
		assertThat(matchingUserList.size()).isEqualTo(1);
		assertThat(matchingUserList.get(0)).usingRecursiveComparison()
			.isEqualTo(MatchingUserResponse.of(matchingUser));
	}

	@Test
	@DisplayName("deleteMatchingUser 메서드에서 matchingId와 userId에 해당하는 matchingUser를 삭제한다.")
	void deleteMatchingUser() {
		//given
		//when
		matchingUserService.deleteMatchingUser(MATCHING_ID, TEST_USER_ID);
		//then
		verify(matchingUserRepository, times(1)).deleteByMatchingIdAndUserId(MATCHING_ID, TEST_USER_ID);
	}

	@Nested
	@DisplayName("createMatchingUser 메서드에서")
	class CreateMatchingUserMethod {

		@Test
		@DisplayName("정상적으로 matchingUser 엔티티가 저장된다.")
		void saveMatchingUserWithoutException() {
			//given
			User user = createUser();
			Matching matching = createMatching();
			given(matchingUserRepository.existsByMatchingIdAndUserId(any(), any())).willReturn(false);

			//when
			MatchingUser matchingUser = matchingUserService.createMatchingUser(matching, user);
			//then
			verify(matchingUserRepository, times(1)).save(matchingUser);
		}

		@Test
		@DisplayName("해당 User가 이미 저장되어 있으면 예외를 던진다.")
		void throwAlreadyJoinMatchingUserException() {
			//given
			User user = createUser();
			Matching matching = createMatching();
			given(matchingUserRepository.existsByMatchingIdAndUserId(any(), any())).willReturn(true);
			//when
			//then
			assertThatThrownBy(() -> matchingUserService.createMatchingUser(matching, user))
				.isInstanceOf(AlreadyJoinMatchingUserException.class);
		}
	}
}