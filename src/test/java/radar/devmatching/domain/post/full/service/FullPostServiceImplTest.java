package radar.devmatching.domain.post.full.service;

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

import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.entity.SubComment;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.response.MainCommentResponse;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.apply.service.ApplyService;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.full.service.dto.UpdatePostDto;
import radar.devmatching.domain.post.full.service.dto.response.PresentPostResponse;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.PostState;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.exception.SimplePostNotFoundException;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.dto.response.SimpleUserResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("FullPostServiceImple 클래스의")
class FullPostServiceImplTest {

	private static final FullPost fullPost = createFullPost();

	@Mock
	private SimplePostService simplePostService;
	@Mock
	private ApplyRepository applyRepository;
	@Mock
	private ApplyService applyService;
	@Mock
	private CommentService commentService;
	private FullPostService fullPostService;

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

	private static SimplePost createSimplePost(User user, Matching matching, FullPost fullPost) {
		SimplePost simplePost = SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.leader(user)
			.matching(matching)
			.fullPost(fullPost)
			.build();
		ReflectionTestUtils.setField(simplePost, "id", 1L);
		return simplePost;
	}

	private static FullPost createFullPost() {
		FullPost fullPost = FullPost.builder()
			.content("내용")
			.build();
		ReflectionTestUtils.setField(fullPost, "id", 1L);
		return fullPost;
	}

	@BeforeEach
	void setup() {
		this.fullPostService = new FullPostServiceImpl(simplePostService, applyRepository, applyService,
			commentService);
	}

	@Nested
	@DisplayName("getPostWithComment 메서드는")
	class GetPostWithCommentMethod {
		@Test
		@DisplayName("정상 흐름이면 simplePost와 applyCount와 Comment를 가져와 PresentPostResponse에 넣어 반환하고, 조회수를 늘린다")
		void correct() throws Exception {
			//given
			User loginUser = createUser();
			SimplePost simplePost = createSimplePost(createUser(), Matching.builder().build(), fullPost);
			MainComment mainComment = MainComment.builder()
				.comment(Comment.builder().content("내용").user(createUser()).build())
				.fullPost(fullPost)
				.build();
			SubComment.builder()
				.mainComment(mainComment)
				.comment(Comment.builder().content("내용").user(loginUser).build())
				.build();
			int applyCount = 2;
			List<MainCommentResponse> mainCommentResponses = List.of(MainCommentResponse.of(mainComment));
			given(simplePostService.findPostById(anyLong())).willReturn(simplePost);
			given(applyService.getAcceptedApplyCount(anyLong())).willReturn(applyCount);
			given(commentService.getAllComments(anyLong())).willReturn(mainCommentResponses);

			//when
			PresentPostResponse presentPostResponse = fullPostService.getPostWithComment(anyLong(), loginUser);

			//then
			verify(simplePostService).findPostById(anyLong());
			verify(applyService).getAcceptedApplyCount(anyLong());
			verify(commentService).getAllComments(anyLong());

			assertThat(presentPostResponse.getSimplePostResponse()).usingRecursiveComparison()
				.isEqualTo(SimplePostResponse.of(simplePost));
			assertThat(presentPostResponse.getPostLeader()).usingRecursiveComparison()
				.isEqualTo(SimpleUserResponse.of(simplePost.getLeader()));
			assertThat(presentPostResponse.getLoginUser()).usingRecursiveComparison()
				.isEqualTo(SimpleUserResponse.of(loginUser));
			assertThat(presentPostResponse.getApplyCount()).isEqualTo(applyCount);
			assertThat(presentPostResponse.getContent()).isEqualTo(simplePost.getFullPost().getContent());
			assertThat(presentPostResponse.getMainCommentResponses()).isEqualTo(mainCommentResponses);

			assertThat(simplePost.getClickCount()).isEqualTo(1);
		}

		@Test
		@DisplayName("simplePostId에 해당하는 게시글이 없을 경우 에러가 발생하고, 조회수가 안 오른다")
		void notExistBySimplePostId() throws Exception {
			//given
			User loginUser = createUser();
			SimplePost simplePost = createSimplePost(loginUser, Matching.builder().build(), fullPost);
			Long clickCount = simplePost.getClickCount();
			willThrow(new SimplePostNotFoundException()).given(simplePostService)
				.findPostById(anyLong());

			//when
			//then
			assertThatThrownBy(() -> fullPostService.getPostWithComment(anyLong(), loginUser))
				.isInstanceOf(SimplePostNotFoundException.class);
			assertThat(clickCount).isEqualTo(simplePost.getClickCount());
			verify(applyService, never()).getAcceptedApplyCount(anyLong());
			verify(commentService, never()).getAllComments(anyLong());
		}
	}

	@Nested
	@DisplayName("getFullPost 메서드는")
	class GetFullPostMethod {

		@Nested
		@DisplayName("simplePostId에 해당하는 게시글이")
		class SimplePostIdParam {

			@Test
			@DisplayName("게시글의 주인 id가 userId와 같을 경우 UpdatePostDto를 반환한다")
			void correct() throws Exception {
				//given
				SimplePost simplePost = createSimplePost(createUser(), Matching.builder().build(), fullPost);
				given(simplePostService.findById(anyLong())).willReturn(simplePost);
				given(simplePostService.findPostById(anyLong())).willReturn(simplePost);

				//when
				UpdatePostDto updatePostDto = fullPostService.getFullPost(anyLong(), simplePost.getLeader().getId());

				//then
				verify(simplePostService).findById(anyLong());
				verify(simplePostService).findPostById(anyLong());
				assertThat(updatePostDto).usingRecursiveComparison().isEqualTo(UpdatePostDto.of(simplePost));
			}

			@Test
			@DisplayName("게시글의 주인 id가 userId와 다를 경우 예외를 반환한다")
			void notLeader() throws Exception {
				//given
				SimplePost simplePost = createSimplePost(createUser(), Matching.builder().build(), fullPost);
				given(simplePostService.findById(anyLong())).willReturn(simplePost);

				//when
				//then
				assertThatThrownBy(() -> fullPostService.getFullPost(anyLong(), 12321L))
					.isInstanceOf(InvalidAccessException.class)
					.hasMessage(ErrorMessage.NOT_LEADER.getMessage());
				verify(simplePostService).findById(anyLong());
				verify(simplePostService, never()).findPostById(anyLong());
			}

			@Test
			@DisplayName("없을 경우 예외를 반환한다.")
			void notExistSimplePost() throws Exception {
				//given
				willThrow(new SimplePostNotFoundException()).given(simplePostService)
					.findById(anyLong());

				//when
				//then
				assertThatThrownBy(() -> fullPostService.getFullPost(1L, 1L))
					.isInstanceOf(SimplePostNotFoundException.class);
				verify(simplePostService).findById(anyLong());
				verify(simplePostService, never()).findPostById(anyLong());
			}
		}
	}

	@Nested
	@DisplayName("updatePost 메서드는")
	class UpdatePostMethod {

		@Nested
		@DisplayName("simplePostId에 해당하는 게시글이")
		class SimplePostIdParam {

			@Test
			@DisplayName("게시글의 주인 id가 userId와 동일하다면 UpdatePostDto의 값을 통해 게시글 엔티티를 업데이트한다")
			void correct() throws Exception {
				//given
				SimplePost simplePost = createSimplePost(createUser(), Matching.builder().build(), fullPost);
				SimplePost updateSimplePost = SimplePost.builder()
					.title("updateTitle")
					.category(PostCategory.MOGAKKO)
					.region(Region.ETC)
					.userNum(18)
					.fullPost(FullPost.builder().content("updateContent").build())
					.leader(createUser())
					.matching(Matching.builder().build())
					.build();
				UpdatePostDto updatePostDto = UpdatePostDto.of(updateSimplePost);
				given(simplePostService.findById(anyLong())).willReturn(simplePost);
				given(simplePostService.findPostById(anyLong())).willReturn(simplePost);

				//when
				fullPostService.updatePost(anyLong(), simplePost.getLeader().getId(), updatePostDto);

				//then
				verify(simplePostService).findById(anyLong());
				verify(simplePostService).findPostById(anyLong());

				assertThat(simplePost.getTitle()).isEqualTo(updatePostDto.getTitle());
				assertThat(simplePost.getCategory()).isEqualTo(updatePostDto.getCategory());
				assertThat(simplePost.getRegion()).isEqualTo(updatePostDto.getRegion());
				assertThat(simplePost.getUserNum()).isEqualTo(updatePostDto.getUserNum());
				assertThat(simplePost.getFullPost().getContent()).isEqualTo(updatePostDto.getContent());
			}

			@Test
			@DisplayName("게시글의 주인 id가 userId와 다를 경우 예외를 반환한다")
			void notLeader() throws Exception {
				//given
				SimplePost simplePost = createSimplePost(createUser(), Matching.builder().build(), fullPost);
				given(simplePostService.findById(anyLong())).willReturn(simplePost);

				//when
				//then
				assertThatThrownBy(() -> fullPostService.updatePost(anyLong(), 12321L, null))
					.isInstanceOf(InvalidAccessException.class)
					.hasMessage(ErrorMessage.NOT_LEADER.getMessage());
				verify(simplePostService).findById(anyLong());
				verify(simplePostService, never()).findPostById(anyLong());
			}

			@Test
			@DisplayName("없을 경우 예외를 반환한다.")
			void notExistSimplePost() throws Exception {
				//given
				willThrow(new SimplePostNotFoundException()).given(simplePostService)
					.findById(anyLong());

				//when
				//then
				assertThatThrownBy(() -> fullPostService.updatePost(0, 0, any(UpdatePostDto.class)))
					.isInstanceOf(SimplePostNotFoundException.class);
				verify(simplePostService).findById(anyLong());
				verify(simplePostService, never()).findPostById(anyLong());
			}
		}
	}

	@Nested
	@DisplayName("deletePost 메서드는")
	class DeletePostMethod {

		@Nested
		@DisplayName("simplePostId에 해당하는 게시글이")
		class SimplePostIdParam {

			@Test
			@DisplayName("게시글의 주인 id가 userId와 동일하다면 게시글을 삭제한다")
			void correct() throws Exception {
				//given
				SimplePost simplePost = createSimplePost(createUser(), Matching.builder().build(), fullPost);
				given(simplePostService.findById(simplePost.getId())).willReturn(simplePost);

				//when
				fullPostService.deletePost(simplePost.getId(), simplePost.getLeader().getId());

				//then
				verify(simplePostService).findById(simplePost.getId());
				verify(simplePostService).deleteById(simplePost.getId());
			}

			@Test
			@DisplayName("게시글의 주인 id가 userId와 다를 경우 예외를 반환한다")
			void notLeader() throws Exception {
				//given
				SimplePost simplePost = createSimplePost(createUser(), Matching.builder().build(), fullPost);
				given(simplePostService.findById(anyLong())).willReturn(simplePost);

				//when
				//then
				assertThatThrownBy(() -> fullPostService.deletePost(anyLong(), 12321L))
					.isInstanceOf(InvalidAccessException.class)
					.hasMessage(ErrorMessage.NOT_LEADER.getMessage());
				verify(simplePostService).findById(anyLong());
				verify(simplePostService, never()).deleteById(anyLong());
			}

			@Test
			@DisplayName("없을 경우 예외를 반환한다.")
			void notExistSimplePost() throws Exception {
				//given
				willThrow(new SimplePostNotFoundException()).given(simplePostService)
					.findById(anyLong());

				//when
				//then
				assertThatThrownBy(() -> fullPostService.deletePost(0, 0))
					.isInstanceOf(SimplePostNotFoundException.class);
				verify(simplePostService).findById(anyLong());
				verify(simplePostService, never()).deleteById(anyLong());
			}
		}
	}

	@Nested
	@DisplayName("closePost 메서드는")
	class ClosePostMethod {

		@Nested
		@DisplayName("simplePostId에 해당하는 게시글이")
		class SimplePostIdParam {

			@Test
			@DisplayName("정상 흐름이면 게시글을 닫는다")
			void correct() throws Exception {
				//given
				SimplePost simplePost = createSimplePost(createUser(), Matching.builder().build(), fullPost);
				given(simplePostService.findById(simplePost.getId())).willReturn(simplePost);
				given(simplePostService.findPostById(simplePost.getId())).willReturn(simplePost);

				//when
				fullPostService.closePost(simplePost.getId(), simplePost.getLeader().getId());

				//then
				verify(simplePostService).findById(simplePost.getId());
				verify(simplePostService).findPostById(simplePost.getId());
				assertThat(simplePost.getPostState()).isEqualTo(PostState.END);
			}

			@Test
			@DisplayName("게시글의 주인 id가 userId와 다를 경우 예외를 반환한다")
			void notLeader() throws Exception {
				//given
				SimplePost simplePost = createSimplePost(createUser(), Matching.builder().build(), fullPost);
				given(simplePostService.findById(anyLong())).willReturn(simplePost);

				//when
				//then
				assertThatThrownBy(() -> fullPostService.closePost(anyLong(), 12321L))
					.isInstanceOf(InvalidAccessException.class)
					.hasMessage(ErrorMessage.NOT_LEADER.getMessage());
				verify(simplePostService).findById(anyLong());
				verify(simplePostService, never()).findPostById(anyLong());
			}

			@Test
			@DisplayName("없을 경우 예외를 반환한다.")
			void notExistSimplePost() throws Exception {
				//given
				willThrow(new SimplePostNotFoundException()).given(simplePostService)
					.findById(anyLong());

				//when
				//then
				assertThatThrownBy(() -> fullPostService.closePost(0, 0))
					.isInstanceOf(SimplePostNotFoundException.class);
				verify(simplePostService).findById(anyLong());
				verify(simplePostService, never()).findPostById(anyLong());
			}
		}
	}
}