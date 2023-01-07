package radar.devmatching.domain.matchings.apply.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
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
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.exception.AlreadyApplyException;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyResponse;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplyService의")
class ApplyServiceTest {

	private static final Long TEST_USER_ID = 1L;
	private static final Long TEST_USER_ID_EX = 2L;
	private static final Long TEST_SIMPLE_POST_ID = 3L;

	@Mock
	UserService userService;
	@Mock
	ApplyRepository applyRepository;
	@Mock
	SimplePostService simplePostService;

	ApplyService applyService;

	@BeforeEach
	void setUp() {
		applyService = new ApplyServiceImpl(applyRepository, simplePostService, userService);
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

	private SimplePost createSimplePost(User user, FullPost fullPost, Matching matching) {
		SimplePost simplePost = SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.leader(user)
			.fullPost(fullPost)
			.matching(matching)
			.build();

		ReflectionTestUtils.setField(simplePost, "id", TEST_SIMPLE_POST_ID);
		return simplePost;
	}

	@Test
	@DisplayName("getAllApplyList 메서드는 userId에 해당하는 apply를 applyResponse로 변환해 반환한다.")
	void getAllApplyList() {
		//given
		User user = createUser();
		Matching matching = Matching.builder().build();
		SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
		Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
		List<Apply> list = new ArrayList<>();
		list.add(apply);
		given(applyRepository.findAppliesByUserId(TEST_USER_ID)).willReturn(list);
		//when
		List<ApplyResponse> applyList = applyService.getAllApplyList(TEST_USER_ID);
		//then
		assertThat(applyList.size()).isEqualTo(1);
		assertThat(applyList.get(0)).usingRecursiveComparison().isEqualTo(ApplyResponse.of(apply));
	}

	@Test
	@DisplayName("simplePostId에 해당하는 Apply 중 Accepted된 Apply 수 반환한다.")
	void getAcceptedApplyCount() {
		//given
		User user = createUser();
		Matching matching = Matching.builder().build();
		SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
		Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
		apply.acceptApply();
		List<Apply> list = new ArrayList<>();
		list.add(apply);
		given(applyRepository.findAllByApplySimplePostId(TEST_SIMPLE_POST_ID)).willReturn(list);
		//when
		int acceptedApplyCount = applyService.getAcceptedApplyCount(TEST_SIMPLE_POST_ID);
		//then
		assertThat(acceptedApplyCount).isEqualTo(1);
	}

	@Nested
	@DisplayName("findById 메서드에서")
	class FindById {

		@Test
		@DisplayName("applyId에 해당하는 apply를 반환한다.")
		void findById() {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
			given(applyRepository.findById(any())).willReturn(Optional.of(apply));
			//when
			Apply findApply = applyService.findById(any());
			//then
			assertThat(findApply).usingRecursiveComparison().isEqualTo(apply);
		}

		@Test
		@DisplayName("applyId에 해당하는 apply가 없으면 예외를 반환한다.")
		void applyIdNotFound() {
			//given
			given(applyRepository.findById(any())).willReturn(Optional.empty());
			//when
			//then
			assertThatThrownBy(() -> applyService.findById(any())).isInstanceOf(EntityNotFoundException.class);
		}
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
			given(userService.findById(any())).willReturn(user);
			given(simplePostService.findById(TEST_SIMPLE_POST_ID)).willReturn(simplePost);
			//when
			Apply apply = applyService.createApply(TEST_SIMPLE_POST_ID, TEST_USER_ID);
			//then
			verify(applyRepository, times(1)).save(apply);
		}

		@Test
		@DisplayName("SimplePost 엔티티에 이미 AuthUser가 신청 되어있으면 예외를 던진다.")
		void authUser_Already_Apply_SimplePost() {
			//given
			User user = createUser();
			Matching matching = Matching.builder().build();
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
			Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
			given(userService.findById(any())).willReturn(user);
			given(simplePostService.findById(TEST_SIMPLE_POST_ID)).willReturn(simplePost);
			given(applyRepository.findByApplySimplePostIdAndApplyUserId(any(), any())).willReturn(
				Optional.of(apply));
			//when
			//then
			assertThatThrownBy(() -> applyService.createApply(TEST_SIMPLE_POST_ID, TEST_USER_ID))
				.isInstanceOf(AlreadyApplyException.class);
			verify(applyRepository, never()).save(any(Apply.class));
		}

	}
}