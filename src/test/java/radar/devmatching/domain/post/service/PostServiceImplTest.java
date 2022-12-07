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

import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.matchings.apply.service.ApplyService;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.service.MatchingService;
import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.post.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService 클래스의")
class PostServiceImplTest {

	private final static User loginUser = createUser();
	private final static Matching matching = Matching.builder().build();
	@Mock
	SimplePostRepository simplePostRepository;
	@Mock
	MatchingService matchingService;
	@Mock
	ApplyService applyService;
	@Mock
	CommentService commentService;
	
	PostService postService;

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
		this.postService = new PostServiceImpl(simplePostRepository, matchingService, applyService, commentService);
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
		List<SimplePostResponse> findPosts = postService.getMyPosts(any(Long.class));

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
		List<SimplePostResponse> findPosts = postService.getApplicationPosts(any(Long.class));

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
				long result = postService.createPost(postRequest, loginUser);

				//then
				assertThat(result).isEqualTo(simplePost.getId());
			}
		}
	}
}