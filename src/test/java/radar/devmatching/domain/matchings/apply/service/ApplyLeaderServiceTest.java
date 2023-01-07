package radar.devmatching.domain.matchings.apply.service;

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

import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.entity.ApplyState;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyLeaderResponse;
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

	private User createUser(Long userId) {
		User user = basicUser();

		ReflectionTestUtils.setField(user, "id", userId);

		return user;
	}

	private SimplePost createSimplePost(User user, FullPost fullPost, Matching matching, Long simplePostId) {
		SimplePost simplePost = basicSimplePost(user, fullPost, matching);

		ReflectionTestUtils.setField(simplePost, "id", simplePostId);

		return simplePost;
	}

	@Test
	@DisplayName("getAllApplyList 메서드에서 findAllByApplySimplePostId로 가져온 리스트를 ApplyLeaderResponse로 변환해서 반환한다.")
	void getAllApplyListMethod() {
		//given
		User user = createUser(TEST_USER_ID);
		Matching matching = Matching.builder().build();
		SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching,
			TEST_SIMPLE_POST_ID);
		Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
		List<Apply> list = new ArrayList<>();
		list.add(apply);
		given(applyRepository.findAllByApplySimplePostId(TEST_SIMPLE_POST_ID)).willReturn(list);
		given(simplePostService.findById(TEST_SIMPLE_POST_ID)).willReturn(simplePost);
		//when
		List<ApplyLeaderResponse> applyList = applyLeaderService.getAllApplyList(TEST_USER_ID, TEST_SIMPLE_POST_ID);
		//then
		assertThat(applyList.size()).isEqualTo(1);
		assertThat(applyList.get(0)).usingRecursiveComparison().isEqualTo(ApplyLeaderResponse.of(apply));
	}

	@Nested
	@DisplayName("acceptApply 메서드에서")
	class AcceptApplyMethod {

		@Test
		@DisplayName("예외를 던지지 않고 정상적으로 Apply 상태를 ACCEPTED 로 변경한다.")
		void accept_Apply_Without_Exception() {
			//given
			User user = createUser(TEST_USER_ID);
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching,
				TEST_SIMPLE_POST_ID);
			Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
			given(simplePostService.findById(TEST_SIMPLE_POST_ID)).willReturn(simplePost);
			given(applyService.findById(TEST_APPLY_ID)).willReturn(apply);

			//when
			applyLeaderService.acceptApply(TEST_USER_ID, TEST_APPLY_ID, TEST_SIMPLE_POST_ID);
			//then
			assertThat(apply.getApplyState()).isEqualTo(ApplyState.ACCEPTED);
		}

	}

	@Nested
	@DisplayName("denyApply 메서드에서")
	class DenyApplyMethod {

		@Test
		@DisplayName("예외를 던지지 않고 정상적으로 Apply 상태를 DENIED 로 변경한다.")
		void deny_Apply_Without_Exception() {
			//given
			User user = createUser(TEST_USER_ID);
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching,
				TEST_SIMPLE_POST_ID);
			Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
			given(simplePostService.findById(TEST_SIMPLE_POST_ID)).willReturn(simplePost);
			given(applyService.findById(TEST_APPLY_ID)).willReturn(apply);
			//when
			applyLeaderService.denyApply(TEST_USER_ID, TEST_APPLY_ID, TEST_SIMPLE_POST_ID);
			//then
			assertThat(apply.getApplyState()).isEqualTo(ApplyState.DENIED);
		}

	}

	@Nested
	@DisplayName("ValidatePermission에서")
	class ValidatePermission {

		@Nested
		@DisplayName("acceptApply와 denyApply 메서드")
		class throwInvalidExceptionWithAcceptApplyMethodAndDenyApplyMethod {
			@Test
			@DisplayName("접근 유저와 SimplePost 리더가 달라 예외를 던진다.")
			void accessUserIdNotEqualSimplePostUserId() {
				//given
				User user = createUser(TEST_USER_ID);
				User userEX = createUser(TEST_USER_ID_EX);
				Matching matching = Matching.builder().build();
				SimplePost simplePost = createSimplePost(userEX, FullPost.builder().content("test").build(), matching,
					TEST_SIMPLE_POST_ID);
				given(simplePostService.findById(TEST_SIMPLE_POST_ID)).willReturn(simplePost);
				//when
				//then
				assertThatThrownBy(
					() -> applyLeaderService.acceptApply(TEST_USER_ID, TEST_APPLY_ID, TEST_SIMPLE_POST_ID))
					.isInstanceOf(InvalidAccessException.class);
				assertThatThrownBy(() -> applyLeaderService.denyApply(TEST_USER_ID, TEST_APPLY_ID, TEST_SIMPLE_POST_ID))
					.isInstanceOf(InvalidAccessException.class);
			}

			@Test
			@DisplayName("apply의 simplePostId와 요청 simplePostId가 달라 예외를 던진다.")
			void accessSimplePostIdNotEqualApplySimplePostId() {
				//given
				User user = createUser(TEST_USER_ID);
				Matching matching = Matching.builder().build();
				SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching,
					TEST_SIMPLE_POST_ID);
				SimplePost simplePostEX = createSimplePost(user, FullPost.builder().content("test").build(),
					matching, TEST_SIMPLE_POST_ID_EX);
				Apply applyEX = Apply.builder().applyUser(user).applySimplePost(simplePostEX).build();
				given(simplePostService.findById(TEST_SIMPLE_POST_ID)).willReturn(simplePost);
				given(applyService.findById(TEST_APPLY_ID)).willReturn(applyEX);
				//when
				//then
				assertThatThrownBy(
					() -> applyLeaderService.acceptApply(TEST_USER_ID, TEST_APPLY_ID, TEST_SIMPLE_POST_ID))
					.isInstanceOf(InvalidAccessException.class);
				assertThatThrownBy(() -> applyLeaderService.denyApply(TEST_USER_ID, TEST_APPLY_ID, TEST_SIMPLE_POST_ID))
					.isInstanceOf(InvalidAccessException.class);
			}
		}
	}

}