package radar.devmatching.domain.matchings.apply.service;

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

import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.exception.AlreadyApplyException;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplyService의")
class ApplyServiceTest {

	private static final Long TEST_USER_ID = 1L;
	private static final Long TEST_USER_ID_EX = 2L;
	private static final Long TEST_SIMPLE_POST_ID = 3L;

	@Mock
	ApplyRepository applyRepository;
	@Mock
	SimplePostRepository simplePostRepository;

	ApplyService applyService;

	@BeforeEach
	void setUp() {
		applyService = new ApplyServiceImpl(applyRepository, simplePostRepository);
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
		void create_Apply_Without_Exception() {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			given(simplePostRepository.findById(any())).willReturn(Optional.of(simplePost));
			//when
			Apply apply = applyService.createApply(TEST_SIMPLE_POST_ID, user);
			//then
			verify(applyRepository, times(1)).save(apply);
		}

		@Test
		@DisplayName("simplePostId를 가지는 simplePost 엔티티가 없으면 예외를 던진다.")
		void simplePost_Entity_Not_Found() {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			given(simplePostRepository.findById(any())).willReturn(Optional.empty());
			//when
			//then
			assertThatThrownBy(() -> applyService.createApply(TEST_SIMPLE_POST_ID, user))
				.isInstanceOf(EntityNotFoundException.class);
			verify(applyRepository, never()).save(any(Apply.class));
		}

		@Test
		@DisplayName("SimplePost 엔티티에 이미 AuthUser가 신청 되어있으면 예외를 던진다.")
		void authUser_Already_Apply_SimplePost() {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
			given(simplePostRepository.findById(any())).willReturn(Optional.of(simplePost));
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
		public void requestUserIdNotEqualAuthUserID() {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
			//when
			//then
			assertThatThrownBy(() -> applyService.getAllApplyList(user, TEST_USER_ID_EX))
				.isInstanceOf(InvalidAccessException.class);
		}
	}
}