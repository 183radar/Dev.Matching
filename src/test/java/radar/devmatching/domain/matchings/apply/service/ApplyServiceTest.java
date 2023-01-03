package radar.devmatching.domain.matchings.apply.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.exception.AlreadyApplyException;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.exception.SimplePostNotFoundException;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplyService의")
class ApplyServiceTest {

	private static final Long TEST_USER_ID = 1L;
	private static final Long TEST_USER_ID_EX = 2L;
	private static final Long TEST_SIMPLE_POST_ID = 3L;

	@Mock
	UserRepository userRepository;
	@Mock
	ApplyRepository applyRepository;
	@Mock
	SimplePostService simplePostService;

	ApplyService applyService;

	@BeforeEach
	void setUp() {
		applyService = new ApplyServiceImpl(userRepository, applyRepository, simplePostService);
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

	private SimplePost createSimplePost(User user, FullPost fullPost, Matching matching) {
		return SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.leader(user)
			.fullPost(fullPost)
			.matching(matching)
			.build();
	}

	@Nested
	@DisplayName("createApply 메서드에서")
	class CreateApplyMethod {

		@Test
		@DisplayName("예외를 던지지 않고 정상적으로 Apply 엔티티가 저장된다")
		void create_Apply_Without_Exception() throws NoSuchFieldException, IllegalAccessException {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			given(simplePostService.findById(any())).willReturn(simplePost);
			//when
			Apply apply = applyService.createApply(TEST_SIMPLE_POST_ID, user);
			//then
			verify(applyRepository, times(1)).save(apply);
		}

		@Test
		@DisplayName("simplePostId를 가지는 simplePost 엔티티가 없으면 예외를 던진다.")
		void simplePost_Entity_Not_Found() throws NoSuchFieldException, IllegalAccessException {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			willThrow(new SimplePostNotFoundException()).given(simplePostService).findById(any());
			//when
			//then
			assertThatThrownBy(() -> applyService.createApply(TEST_SIMPLE_POST_ID, user))
				.isInstanceOf(SimplePostNotFoundException.class);
			verify(applyRepository, never()).save(any(Apply.class));
		}

		@Test
		@DisplayName("SimplePost 엔티티에 이미 AuthUser가 신청 되어있으면 예외를 던진다.")
		void authUser_Already_Apply_SimplePost() throws NoSuchFieldException, IllegalAccessException {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
			given(simplePostService.findById(any())).willReturn(simplePost);
			given(applyRepository.findByApplySimplePostIdAndApplyUserId(any(), any())).willReturn(
				Optional.of(apply));
			//when
			//then
			assertThatThrownBy(() -> applyService.createApply(TEST_SIMPLE_POST_ID, user))
				.isInstanceOf(AlreadyApplyException.class);
			verify(applyRepository, never()).save(any(Apply.class));
		}

	}

	@Nested
	@DisplayName("getAllApplyList 메서드에서")
	class GetAllApplyListMethod {

		@Test
		@DisplayName("요청 userId와 User 엔티티의 id가 다르면 예외를 던진다.")
		public void requestUserIdNotEqualAuthUserID() throws NoSuchFieldException, IllegalAccessException {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
			//when
			//then
			assertThatThrownBy(() -> applyService.getAllApplyList(user))
				.isInstanceOf(InvalidAccessException.class);
		}
	}
}