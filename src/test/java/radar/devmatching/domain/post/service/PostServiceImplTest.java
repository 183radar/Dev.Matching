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

import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.FullPostRepository;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.post.service.dto.CreatePostDto;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;

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

	@Mock
	SimplePostRepository simplePostRepository;
	@Mock
	FullPostRepository fullPostRepository;
	@Mock
	UserRepository userRepository;

	@InjectMocks
	PostServiceImpl postService;

	private SimplePost createSimplePost() {
		return SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.user(loginUser)
			.fullPost(FullPost.builder().content("내용").build())
			.build();
	}

	private CreatePostDto createPostDto() {
		return CreatePostDto.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.content("내용")
			.build();
	}

	@Test
	@DisplayName("getMyPost메서드는 내 유저 아이디가 인자로 들어오면 내가 게시한 게시글들을 반환한다")
	void getMyPostTest() throws Exception {
		//given
		SimplePost simplePost = createSimplePost();
		given(simplePostRepository.findMyPostByUserId(any())).willReturn(List.of(simplePost));

		//when
		List<SimplePost> myPost = postService.getMyPosts(1L);

		//then
		assertThat(simplePost).isEqualTo(myPost.get(0));
	}

	@Nested
	@DisplayName("createPost 메서드는")
	class CreatePostMethod {

		@Nested
		@DisplayName("게시글 정보가 포함된 createPostDto가 인자로 들어오면")
		class ReceivedCreatePostDtoWhichContainsPostInfo {

			@Test
			@DisplayName("게시글을 저장한다.")
			void savePost() throws Exception {
				//given
				CreatePostDto createPostDto = createPostDto();
				SimplePost simplePost = createSimplePost();

				given(simplePostRepository.save(any(SimplePost.class))).willReturn(simplePost);

				//when
				SimplePost createdPost = postService.createPost(createPostDto, loginUser);

				//then
				assertThat(createdPost).usingRecursiveComparison().isEqualTo(createPostDto.toEntity(loginUser));
			}
		}
	}
}