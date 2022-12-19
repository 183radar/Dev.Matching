package radar.devmatching.domain.matchings.apply.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.entity.ApplyState;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserService;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplyLeaderService의")
class ApplyLeaderServiceTest {

	private static final Long TEST_USER_ID = 1L;
	private static final Long TEST_USER_ID_EX = 2L;
	private static final Long TEST_SIMPLE_POST_ID = 3L;
	private static final Long TEST_SIMPLE_POST_ID_EX = 4L;
	private static final Long TEST_APPLY_ID = 5L;

	@Mock
	ApplyRepository applyRepository;

	@Mock
	ApplyService applyService;

	@Mock
	MatchingUserService matchingUserService;

	@Mock
	SimplePostService simplePostService;

	ApplyLeaderService applyLeaderService;

	private static User basicUser() {
		User user = User.builder()
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("schoolName")
			.githubUrl("githubUrl")
			.introduce("introduce")
			.build();
		return user;
	}

	private static SimplePost basicSimplePost(User user, FullPost fullPost, Matching matching) {
		SimplePost simplePost = SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.leader(user)
			.fullPost(fullPost)
			.matching(matching)
			.build();
		return simplePost;
	}

	@BeforeEach
	void setUp() {
		applyLeaderService = new ApplyLeaderServiceImpl(applyRepository, applyService, matchingUserService,
			simplePostService);
	}

	private User createUser() throws IllegalAccessException, NoSuchFieldException {
		User user = basicUser();

		Class<User> userClass = User.class;
		Field userId = userClass.getDeclaredField("id");
		userId.setAccessible(true);
		userId.set(user, TEST_USER_ID);

		return user;
	}

	private User createUserEX() throws IllegalAccessException, NoSuchFieldException {
		User user = basicUser();

		Class<User> userClass = User.class;
		Field userId = userClass.getDeclaredField("id");
		userId.setAccessible(true);
		userId.set(user, TEST_USER_ID_EX);

		return user;
	}

	private SimplePost createSimplePost(User user, FullPost fullPost, Matching matching) throws
		NoSuchFieldException,
		IllegalAccessException {
		SimplePost simplePost = basicSimplePost(user, fullPost, matching);

		Class<SimplePost> simplePostClass = SimplePost.class;
		Field simplePostId = simplePostClass.getDeclaredField("id");
		simplePostId.setAccessible(true);
		simplePostId.set(simplePost, TEST_SIMPLE_POST_ID);

		return simplePost;
	}

	private SimplePost createSimplePostEX(User user, FullPost fullPost, Matching matching) throws
		NoSuchFieldException,
		IllegalAccessException {
		SimplePost simplePost = basicSimplePost(user, fullPost, matching);

		Class<SimplePost> simplePostClass = SimplePost.class;
		Field simplePostId = simplePostClass.getDeclaredField("id");
		simplePostId.setAccessible(true);
		simplePostId.set(simplePost, TEST_SIMPLE_POST_ID_EX);

		return simplePost;
	}

	@Nested
	@DisplayName("acceptApply 메서드에서")
	class AcceptApplyMethod {

		@Test
		@DisplayName("예외를 던지지 않고 정상적으로 Apply 상태를 ACCEPTED 로 변경한다.")
		void accept_Apply_Without_Exception() throws NoSuchFieldException, IllegalAccessException {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
			given(simplePostService.getSimplePostOnly(TEST_SIMPLE_POST_ID)).willReturn(simplePost);
			given(applyService.getApply(TEST_APPLY_ID)).willReturn(apply);

			//when
			applyLeaderService.acceptApply(user, TEST_APPLY_ID, TEST_SIMPLE_POST_ID);
			//then
			assertThat(apply.getApplyState()).isEqualTo(ApplyState.ACCEPTED);
		}

	}

	@Nested
	@DisplayName("denyApply 메서드에서")
	class DenyApplyMethod {

		@Test
		@DisplayName("예외를 던지지 않고 정상적으로 Apply 상태를 DENIED 로 변경한다.")
		void deny_Apply_Without_Exception() throws NoSuchFieldException, IllegalAccessException {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
			given(simplePostService.getSimplePostOnly(TEST_SIMPLE_POST_ID)).willReturn(simplePost);
			given(applyService.getApply(TEST_APPLY_ID)).willReturn(apply);
			//when
			applyLeaderService.denyApply(user, TEST_APPLY_ID, TEST_SIMPLE_POST_ID);
			//then
			assertThat(apply.getApplyState()).isEqualTo(ApplyState.DENIED);
		}

	}

	@Nested
	@DisplayName("getAllApplyList 메서드에서")
	class GetAllApplyListMethod {

	}

	@Nested
	@DisplayName("ValidatePermission에서")
	class ValidatePermission {

		@Nested
		@DisplayName("acceptApply와 denyApply 메서드")
		class throwInvalidExceptionWithAcceptApplyMethodAndDenyApplyMethod {
			@Test
			@DisplayName("접근 유저와 SimplePost 리더가 달라 예외를 던진다.")
			void accessUserIdNotEqualSimplePostUserId() throws NoSuchFieldException, IllegalAccessException {
				//given
				User user = createUser();
				User userEX = createUserEX();
				Matching matching = Matching.builder().build();
				SimplePost simplePost = createSimplePost(userEX, FullPost.builder().content("test").build(), matching);
				given(simplePostService.getSimplePostOnly(TEST_SIMPLE_POST_ID)).willReturn(simplePost);
				//when
				//then
				assertThatThrownBy(() -> applyLeaderService.acceptApply(user, TEST_APPLY_ID, TEST_SIMPLE_POST_ID))
					.isInstanceOf(InvalidAccessException.class);
				assertThatThrownBy(() -> applyLeaderService.denyApply(user, TEST_APPLY_ID, TEST_SIMPLE_POST_ID))
					.isInstanceOf(InvalidAccessException.class);
			}

			@Test
			@DisplayName("apply의 simplePostId와 요청 simplePostId가 달라 예외를 던진다.")
			void accessSimplePostIdNotEqualApplySimplePostId() throws NoSuchFieldException, IllegalAccessException {
				//given
				User user = createUser();
				Matching matching = Matching.builder().build();
				SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
				SimplePost simplePostEX = createSimplePostEX(user, FullPost.builder().content("test").build(),
					matching);
				Apply applyEX = Apply.builder().applyUser(user).applySimplePost(simplePostEX).build();
				given(simplePostService.getSimplePostOnly(TEST_SIMPLE_POST_ID)).willReturn(simplePost);
				given(applyService.getApply(TEST_APPLY_ID)).willReturn(applyEX);
				//when
				//then
				assertThatThrownBy(() -> applyLeaderService.acceptApply(user, TEST_APPLY_ID, TEST_SIMPLE_POST_ID))
					.isInstanceOf(InvalidAccessException.class);
				assertThatThrownBy(() -> applyLeaderService.denyApply(user, TEST_APPLY_ID, TEST_SIMPLE_POST_ID))
					.isInstanceOf(InvalidAccessException.class);
			}
		}
	}

}