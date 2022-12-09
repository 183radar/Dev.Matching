package radar.devmatching.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.service.MatchingService;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.repository.SimplePostRepository;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.post.simple.service.SimplePostServiceImpl;
import radar.devmatching.domain.post.simple.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimplePostService 클래스의")
class SimplePostServiceImplTest {

	private final static User loginUser = createUser();
	private final static Matching matching = Matching.builder().build();

	@Mock
	SimplePostRepository simplePostRepository;
	@Mock
	MatchingService matchingService;

	SimplePostService simplePostService;

	private static User createUser() {
		User user = User.builder()
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("schoolName")
			.githubUrl("githubUrl")
			.introduce("introduce")
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);
		return user;
	}

	@BeforeEach
	void setup() {
		this.simplePostService = new SimplePostServiceImpl(simplePostRepository, matchingService);
	}

	private SimplePost createSimplePost(User user, Matching matching) {
		return SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.leader(user)
			.matching(matching)
			.fullPost(FullPost.builder().content("내용").build())
			.build();
	}

	private CreatePostRequest createPostRequest() {
		return CreatePostRequest.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.content("내용")
			.build();
	}

	@Test
	@DisplayName("getMyPosts메서드는 내 유저 아이디가 인자로 들어오면 내가 게시한 SimplePost들을 반환한다")
	void getMyPostsTest() throws Exception {
		//given
		SimplePost myPost = createSimplePost(loginUser, matching);
		given(simplePostRepository.findMyPostsByLeaderIdOrderByCreateDateDesc(any(Long.class)))
			.willReturn(List.of(myPost));

		//when
		List<SimplePostResponse> findPosts = simplePostService.getMyPosts(any(Long.class));

		//then
		assertThat(SimplePostResponse.of(myPost)).usingRecursiveComparison().isEqualTo(findPosts.get(0));
	}

	@Test
	@DisplayName("getApplicationPosts 메서드는 내 유저 아이디가 인자로 들어오면 내가 신청한 SimplePost들을 반환한다.")
	void getApplicationPostsTest() throws Exception {
		//given
		SimplePost appliedPost = createSimplePost(loginUser, matching);
		given(simplePostRepository.findApplicationPosts(any(Long.class))).willReturn(List.of(appliedPost));

		//when
		List<SimplePostResponse> findPosts = simplePostService.getApplicationPosts(any(Long.class));

		//then
		assertThat(SimplePostResponse.of(appliedPost)).usingRecursiveComparison().isEqualTo(findPosts.get(0));
	}

	@Nested
	@DisplayName("createPost 메서드는")
	class CreatePostMethod {

		@Nested
		@DisplayName("게시글 정보가 포함된 createPostRequest가 인자로 들어오면")
		class ReceivedCreatePostRequestWhichContainsPostInfo {

			@Test
			@DisplayName("매칭을 만들고 SimplePost와 FullPost를 저장한다")
			void savePost() throws Exception {
				//given
				SimplePost simplePost = createSimplePost(loginUser, matching);
				ReflectionTestUtils.setField(simplePost, "id", 1L);
				CreatePostRequest postRequest = createPostRequest();
				given(matchingService.createMatching(loginUser)).willReturn(matching);
				given(simplePostRepository.save(any(SimplePost.class))).willReturn(simplePost);

				//when
				long result = simplePostService.createPost(postRequest, loginUser);

				//then
				assertThat(result).isEqualTo(simplePost.getId());
			}
		}
	}
}