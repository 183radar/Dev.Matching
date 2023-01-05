package radar.devmatching.domain.post.simple.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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

import radar.devmatching.common.exception.InvalidParamException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.service.MatchingLeaderService;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.PostState;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.exception.SimplePostNotFoundException;
import radar.devmatching.domain.post.simple.repository.SimplePostRepository;
import radar.devmatching.domain.post.simple.service.dto.MainPostDto;
import radar.devmatching.domain.post.simple.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimplePostService 클래스의")
class SimplePostServiceImplTest {

	private final static User loginUser = createUser();
	private final static Matching matching = Matching.builder().build();

	@Mock
	UserService userService;
	@Mock
	SimplePostRepository simplePostRepository;
	@Mock
	MatchingLeaderService matchingLeaderService;

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
		this.simplePostService = new SimplePostServiceImpl(userService, simplePostRepository, matchingLeaderService);
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
		assertThat(findPosts.get(0)).usingRecursiveComparison().isEqualTo(SimplePostResponse.of(myPost));
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
	@DisplayName("findById 메서드는")
	class FindByIdMethod {

		@Test
		@DisplayName("db에 존재하지 않는 simplePostId를 넘기면 에러를 낸다.")
		void ifNotExistIdThanThrowException() throws Exception {
			//given
			//when
			//then
			assertThatThrownBy(() -> simplePostService.findById(anyLong()))
				.isInstanceOf(SimplePostNotFoundException.class);
		}

		@Test
		@DisplayName("db에 존재하는 simplePostId를 넘기면 simplePost만 가져온다.")
		void ifExistIdThanGetSimplePost() throws Exception {
			//given
			SimplePost simplePost = createSimplePost(loginUser, matching);
			given(simplePostRepository.findById(anyLong())).willReturn(Optional.of(simplePost));

			//when
			SimplePost findSimplePost = simplePostService.findById(anyLong());

			//then
			assertThat(findSimplePost).isEqualTo(simplePost);
		}
	}

	@Nested
	@DisplayName("findPostById 메서드는")
	class FindPostById {

		@Test
		@DisplayName("db에 존재하지 않는 simplePostId를 넘기면 에러를 낸다.")
		void ifNotExistIdThanThrowException() throws Exception {
			//given
			//when
			//then
			assertThatThrownBy(() -> simplePostService.findPostById(anyLong()))
				.isInstanceOf(SimplePostNotFoundException.class);
		}

		@Test
		@DisplayName("db에 존재하는 simplePostId를 넘기면 simplePost만 가져온다.")
		void ifExistIdThanGetSimplePost() throws Exception {
			//given
			SimplePost simplePost = createSimplePost(loginUser, matching);
			given(simplePostRepository.findPostById(anyLong())).willReturn(Optional.of(simplePost));

			//when
			SimplePost findSimplePost = simplePostService.findPostById(anyLong());

			//then
			assertThat(findSimplePost).isEqualTo(simplePost);
		}
	}

	@Nested
	@DisplayName("getMainPostDto 메서드는")
	class GetMainPostDtoMethod {

		@Nested
		@DisplayName("postCategoryParam 인자가")
		class PostCategoryParameter {

			@Test
			@DisplayName("PostCategory에 존재하지 않는 인자면 에러를 던진다")
			void isNotMatchWithPostCategoryThanThrow() throws Exception {
				//given
				//when
				//then
				assertThatThrownBy(() -> simplePostService.getMainPostDto(loginUser.getId(), "noMatch"))
					.isInstanceOf(InvalidParamException.class)
					.hasMessage(ErrorMessage.INVALID_POST_CATEGORY.getMessage());
			}

			@Test
			@DisplayName("ALL로 들어오면 simplePostRepository.findByPostStateOrderByCreateDateDesc 메서드로 게시글들을 가져온다 ")
			void isAllThanSearchAll() throws Exception {
				//given
				SimplePost simplePost = createSimplePost(loginUser, matching);
				given(userService.getUserEntity(anyLong())).willReturn(loginUser);
				given(simplePostRepository.findByPostStateOrderByCreateDateDesc(PostState.RECRUITING))
					.willReturn(List.of(simplePost));

				//when
				MainPostDto findMainPostDto = simplePostService.getMainPostDto(loginUser.getId(), "ALL");

				//then
				assertThat(findMainPostDto.getSimplePostResponses().get(0))
					.usingRecursiveComparison().isEqualTo(SimplePostResponse.of(simplePost));
			}

			@Test
			@DisplayName("PostCategory에 존재하는 값이면 카테고리에 해당하는 게시글을 반환한다.")
			void correctCase() throws Exception {
				//given
				SimplePost simplePost = createSimplePost(loginUser, matching);
				given(userService.getUserEntity(anyLong())).willReturn(loginUser);
				given(simplePostRepository.findByCategoryAndPostStateOrderByCreateDateDesc(
					PostCategory.PROJECT, PostState.RECRUITING)).willReturn(List.of(simplePost));

				//when
				MainPostDto findMainPostDto = simplePostService.getMainPostDto(loginUser.getId(), "PROJECT");

				//then
				assertThat(findMainPostDto.getSimplePostResponses().get(0).getCategory()).isEqualTo(
					PostCategory.PROJECT);
			}
		}
	}

	@Nested
	@DisplayName("searchSimplePost 메서드는")
	class searchSimplePostMethod {

		@Test
		@DisplayName("유저에서 닉네임을 가져오고, dto에서 지역을 가져오고, 리포지토리에서 서칭 조건에 따라 게시글을 가져와 MainPostDto로 반환한다")
		void correctCase() throws Exception {
			//given
			SimplePost simplePost = createSimplePost(loginUser, matching);
			MainPostDto mainPostDto = MainPostDto.builder().region(Region.BUSAN).build();
			given(userService.getUserEntity(anyLong())).willReturn(loginUser);
			given(simplePostRepository.findRecruitingPostBySearchCondition("ALL", mainPostDto))
				.willReturn(List.of(simplePost));

			//when
			MainPostDto findMainPostDto = simplePostService.searchSimplePost(loginUser.getId(), "ALL", mainPostDto);

			//then
			assertThat(findMainPostDto.getNickname()).isEqualTo(loginUser.getNickName());
			assertThat(findMainPostDto.getRegion()).isEqualTo(mainPostDto.getRegion());
			assertThat(findMainPostDto.getSimplePostResponses().get(0))
				.usingRecursiveComparison().isEqualTo(SimplePostResponse.of(simplePost));
		}
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
				given(userService.getUserEntity(anyLong())).willReturn(loginUser);
				given(matchingLeaderService.createMatching(loginUser)).willReturn(matching);
				given(simplePostRepository.save(any(SimplePost.class))).willReturn(simplePost);

				//when
				long result = simplePostService.createPost(postRequest, loginUser.getId());

				//then
				assertThat(result).isEqualTo(simplePost.getId());
			}
		}
	}
}