package radar.devmatching.domain.matchings.matching.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.repository.MatchingRepository;
import radar.devmatching.domain.matchings.matching.service.dto.MatchingUpdate;
import radar.devmatching.domain.matchings.matching.service.dto.response.MatchingInfoResponse;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUserRole;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserService;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchingService의")
public class MatchingServiceTest {

	private static final Long TEST_USER_ID = 1L;
	private static final Long MATCHING_ID = 2L;
	private static final Long MATCHING_ID_EX = 3L;

	@Mock
	MatchingRepository matchingRepository;
	@Mock
	MatchingUserService matchingUserService;

	@Mock
	UserService userService;

	MatchingService matchingService;

	@BeforeEach
	void setUp() {
		matchingService = new MatchingServiceImpl(matchingRepository, matchingUserService, userService);
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
	@DisplayName("getMatchingInfo 메서드에서 matchingId와 userId에 해당하는 ")
	void getMatchingInfo() {
		//given
		User user = createUser();
		Matching matching = createMatching();
		MatchingUser matchingUser = MatchingUser.builder()
			.user(user)
			.matchingUserRole(MatchingUserRole.LEADER)
			.matching(matching)
			.build();
		given(matchingUserService.findByMatchingIdAndUserId(matching.getId(), user.getId())).willReturn(matchingUser);
		given(userService.findById(user.getId())).willReturn(user);
		//when
		MatchingInfoResponse matchingInfoResponse = matchingService.getMatchingInfo(matching.getId(), user.getId());
		//then
		assertThat(matchingInfoResponse).usingRecursiveComparison().isEqualTo(MatchingInfoResponse.of(matchingUser));
	}

	@Test
	@DisplayName("getMatchingUpdateData 메서드는 matchingId에 해당하는 Matching을 MatchingUpdate로 변환해 반환한다.")
	void getMatchingUpdateData() {
		//given
		Matching matching = createMatching();
		given(matchingRepository.findById(matching.getId())).willReturn(Optional.of(matching));
		//when
		MatchingUpdate updateData = matchingService.getMatchingUpdateData(matching.getId());
		//then
		assertThat(updateData).usingRecursiveComparison().isEqualTo(MatchingUpdate.of(matching));
	}

	@Nested
	@DisplayName("findById 메서드에서")
	class FindById {

		@Test
		@DisplayName("matchingId에 해당하는 객체를 반환한다.")
		void findById() {
			//given
			Matching matching = createMatching();
			given(matchingRepository.findById(matching.getId())).willReturn(Optional.of(matching));
			//when
			Matching findMatching = matchingService.findById(matching.getId());
			//then
			assertThat(findMatching).usingRecursiveComparison().isEqualTo(matching);
		}

		@Test
		@DisplayName("matchingId에 해당하는 객체가 없으면 예외를 던진다..")
		void matchingEntityNotFound() {
			//given
			given(matchingRepository.findById(anyLong())).willReturn(Optional.empty());
			//when
			//then
			assertThatThrownBy(() -> matchingService.findById(anyLong())).isInstanceOf(EntityNotFoundException.class);
		}
	}

	@Nested
	@DisplayName("updateMatching 메서드에서")
	class UpdateMatching {

		@Test
		@DisplayName("정상 흐름이면 matching의 정보가 변경된다.")
		void updateMatching() {
			//given
			Matching matching = createMatching();
			MatchingUpdate matchingUpdate = MatchingUpdate.builder()
				.matchingId(MATCHING_ID)
				.matchingTitle("test")
				.matchingInfo("test")
				.build();
			given(matchingRepository.findById(matching.getId())).willReturn(Optional.of(matching));
			//when
			matchingService.updateMatching(matching.getId(), matchingUpdate);
			//then
			assertThat(matching.getMatchingTitle()).isEqualTo(matchingUpdate.getMatchingTitle());
			assertThat(matching.getMatchingInfo()).isEqualTo(matchingUpdate.getMatchingInfo());
		}

		@Test
		@DisplayName("요청 matchingId와 matchingUpdate의 matchingId가 다르면 예외를 던진다.")
		void requestMatchingId_NotEqual_MatchingUpdate_MatchingId() {
			//given
			Matching matching = createMatching();
			MatchingUpdate matchingUpdate = MatchingUpdate.builder()
				.matchingId(MATCHING_ID_EX)
				.matchingTitle("test")
				.matchingInfo("test")
				.build();
			given(matchingRepository.findById(matching.getId())).willReturn(Optional.of(matching));
			//when
			//then
			assertThatThrownBy(() -> matchingService.updateMatching(matching.getId(), matchingUpdate))
				.isInstanceOf(InvalidAccessException.class);
		}
	}
}
