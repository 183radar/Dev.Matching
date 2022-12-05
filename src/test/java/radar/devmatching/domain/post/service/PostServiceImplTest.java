package radar.devmatching.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserLeaderService;
import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.FullPostRepository;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.post.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService 클래스의")
class PostServiceImplTest {

	private final static User loginUser = User.builder()
		.username("username")
		.password("password")
		.nickName("nickName")
		.schoolName("schoolName")
		.githubUrl("githubUrl")
		.introduce("introduce")
		.build();

	private final static Matching matching = Matching.builder().build();

	@Mock
	SimplePostRepository simplePostRepository;
	@Mock
	FullPostRepository fullPostRepository;
	@Mock
	MatchingUserLeaderService matchingUserLeaderService;

	@InjectMocks
	PostServiceImpl postService;

	private SimplePost createSimplePost() {
		return SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.leader(loginUser)
			.matching(matching)
			.fullPost(FullPost.builder().content("내용").build())
			.build();
	}

	private CreatePostRequest createPostDto() {
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
		SimplePost myPost = createSimplePost();
		given(simplePostRepository.findMyPostsByLeaderIdOrderByCreateDateDesc(1L)).willReturn(List.of(myPost));

		//when
		List<SimplePostResponse> findPosts = postService.getMyPosts(1L);

		//then
		assertThat(SimplePostResponse.of(myPost)).usingRecursiveComparison().isEqualTo(findPosts.get(0));
	}

	@Test
	@DisplayName("getApplicationPosts 메서드는 내 유저 아이디가 인자로 들어오면 내가 신청한 SimplePost들을 반환한다.")
	void getApplicationPostsTest() throws Exception {
		//given
		SimplePost appliedPost = createSimplePost();
		given(simplePostRepository.findApplicationPosts(1L)).willReturn(List.of(appliedPost));

		//when
		List<SimplePostResponse> findPosts = postService.getApplicationPosts(1L);

		//then
		assertThat(SimplePostResponse.of(appliedPost)).usingRecursiveComparison().isEqualTo(findPosts.get(0));
	}

	@Nested
	@DisplayName("createPost 메서드는")
	class CreatePostMethod {

		@Nested
		@DisplayName("게시글 정보가 포함된 createPostDto가 인자로 들어오면")
		class ReceivedCreatePostDtoWhichContainsPostInfo {

			@Test
			@DisplayName("Matching, Post 저장 후 매칭유저리더를 만든다")
			void savePost() throws Exception {
				//given
				CreatePostRequest createPostDto = createPostDto();
				SimplePost simplePost = createSimplePost();
				given(simplePostRepository.save(any(SimplePost.class))).willReturn(simplePost);

				//when

				SimplePost createdPost = postService.createPost(createPostDto, loginUser);

				//then
				assertThat(createdPost).usingRecursiveComparison()
					.isEqualTo(createPostDto.toEntity(loginUser, matching));
			}
		}
	}
}