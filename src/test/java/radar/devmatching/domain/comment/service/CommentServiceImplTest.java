package radar.devmatching.domain.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.entity.SubComment;
import radar.devmatching.domain.comment.repository.MainCommentRepository;
import radar.devmatching.domain.comment.repository.SubCommentRepository;
import radar.devmatching.domain.comment.service.dto.UpdateCommentDto;
import radar.devmatching.domain.comment.service.dto.request.CreateCommentRequest;
import radar.devmatching.domain.comment.service.dto.response.MainCommentResponse;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.exception.SimplePostNotFoundException;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService 클래스의")
class CommentServiceImplTest {

	// private final static User loginUser = createUser();

	@Mock
	SimplePostService simplePostService;
	@Mock
	MainCommentRepository mainCommentRepository;
	@Mock
	SubCommentRepository subCommentRepository;

	CommentService commentService;

	private static User createUser(long userId) {
		User user = User.builder()
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("schoolName")
			.githubUrl("githubUrl")
			.introduce("introduce")
			.build();
		ReflectionTestUtils.setField(user, "id", userId);
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

	@BeforeEach
	void setUp() {
		this.commentService = new CommentServiceImpl(simplePostService, mainCommentRepository, subCommentRepository);
	}

	private MainComment createMainComment(FullPost fullPost, User user) {
		MainComment mainComment = MainComment.builder()
			.fullPost(fullPost)
			.comment(createComment(user))
			.build();
		ReflectionTestUtils.setField(mainComment, "id", 1L);
		return mainComment;
	}

	private SubComment createSubComment(MainComment mainComment, User user) {
		SubComment subComment = SubComment.builder()
			.comment(createComment(user))
			.mainComment(mainComment)
			.build();
		ReflectionTestUtils.setField(subComment, "id", 1L);
		return subComment;
	}

	private Comment createComment(User user) {
		return Comment.builder()
			.user(user)
			.content("내용")
			.build();
	}

	@Nested
	@DisplayName("createMainComment 메서드는")
	class CreateMainCommentMethod {

		@Test
		@DisplayName("simplePostId에 해당하는 게시글 엔티티가 없을 경우 예외를 반환한다")
		void simplePostNotExistThanThrow() throws Exception {
			//given
			willThrow(new SimplePostNotFoundException()).given(simplePostService)
				.findById(anyLong());

			//when
			//then
			assertThatThrownBy(() -> commentService.createMainComment(1L, any(User.class), null))
				.isInstanceOf(SimplePostNotFoundException.class);
			verify(mainCommentRepository, never()).save(any(MainComment.class));
		}

		@Test
		@DisplayName("정상 흐름일 경우 MainComment를 저장한다")
		void correctThanSaveMainComment() throws Exception {
			//given
			User loginUser = createUser(1L);
			CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
				.content("내용")
				.commentType(CreateCommentRequest.CommentType.MAIN)
				.build();
			SimplePost simplePost = createSimplePost(loginUser, Matching.builder().build(), FullPost.builder().build());
			given(simplePostService.findById(simplePost.getId())).willReturn(simplePost);

			//when
			commentService.createMainComment(simplePost.getId(), loginUser, createCommentRequest);

			//then
			verify(simplePostService).findById(simplePost.getId());
			verify(mainCommentRepository).save(any(MainComment.class));
		}
	}

	@Nested
	@DisplayName("createSubComment 메서드는")
	class CreateSubCommentMethod {

		@Test
		@DisplayName("mainCommentId에 해당하는 댓글 엔티티가 없을 경우 예외를 반환한다")
		void mainCommentNotExistThanThrow() throws Exception {
			//given
			//when
			//then
			assertThatThrownBy(() -> commentService.createSubComment(1L, any(User.class), null))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorMessage.MAIN_COMMENT_NOT_FOUND.getMessage());
			verify(subCommentRepository, never()).save(any(SubComment.class));
		}

		@Test
		@DisplayName("정상 흐름일 경우 SubComment 엔티티를 저장한다")
		void correct() throws Exception {
			//given
			User loginUser = createUser(1L);
			FullPost fullPost = FullPost.builder().content("내용").build();
			SimplePost simplePost = createSimplePost(loginUser, Matching.builder().build(), fullPost);
			MainComment mainComment = createMainComment(fullPost, loginUser);
			SubComment subComment = createSubComment(mainComment, loginUser);
			CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
				.content("내용")
				.commentType(CreateCommentRequest.CommentType.SUB)
				.build();
			given(mainCommentRepository.findMainCommentById(mainComment.getId())).willReturn(Optional.of(mainComment));
			given(subCommentRepository.save(any(SubComment.class))).willReturn(subComment);
			given(subCommentRepository.findBySimplePostIdAsSubCommentId(subComment.getId())).willReturn(
				simplePost.getId());

			//when
			long simplePostId = commentService.createSubComment(mainComment.getId(), loginUser, createCommentRequest);

			//then
			assertThat(simplePostId).isEqualTo(simplePost.getId());
			verify(mainCommentRepository).findMainCommentById(mainComment.getId());
			verify(subCommentRepository).save(any(SubComment.class));
			verify(subCommentRepository).findBySimplePostIdAsSubCommentId(subComment.getId());
		}
	}

	@Nested
	@DisplayName("getAllComments 메서드는")
	class GetAppCommentMethod {
		@Test
		@DisplayName("정상 흐름이면 fullPostId와 연결된 MainComments 엔티티를 가져온다")
		void correct() throws Exception {
			//given
			FullPost fullPost = FullPost.builder().content("내용").build();
			ReflectionTestUtils.setField(fullPost, "id", 1L);
			MainComment mainComment = createMainComment(fullPost, createUser(1L));
			createSubComment(mainComment, createUser(2L));
			given(mainCommentRepository.getAllComments(fullPost.getId())).willReturn(List.of(mainComment));

			//when
			List<MainCommentResponse> allComments = commentService.getAllComments(fullPost.getId());

			//then
			assertThat(allComments.get(0)).usingRecursiveComparison().isEqualTo(MainCommentResponse.of(mainComment));
		}
	}

	@Nested
	@DisplayName("mainCommentExistById 메서드는")
	class MainCommentExistByIdMethod {

		@Test
		@DisplayName("mainCommentId에 해당하는 엔티티가 없으면 에러를 던진다")
		void notExistMainCommentThanReturnThrow() throws Exception {
			//given
			//when
			//then
			assertThatThrownBy(() -> commentService.mainCommentExistById(anyLong()))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorMessage.MAIN_COMMENT_NOT_FOUND.getMessage());
		}

		@Test
		void existMainComment() throws Exception {
			//given
			given(mainCommentRepository.existsById(anyLong())).willReturn(true);
			//when
			//then
			Assertions.assertDoesNotThrow(() -> commentService.mainCommentExistById(anyLong()));
		}
	}

	@Nested
	@DisplayName("getMainCommentOnly 메서드는")
	class GetMainCommentOnlyMethod {
		@Test
		@DisplayName("정상 흐름이면 mainComment를 담은 UpdateCommentDto를 반환한다")
		void correct() throws Exception {
			//given
			MainComment mainComment = createMainComment(FullPost.builder().build(), createUser(1L));
			given(mainCommentRepository.findMainCommentById(mainComment.getId()))
				.willReturn(Optional.of(mainComment));

			//when
			UpdateCommentDto updateCommentDto = commentService.getMainCommentOnly(mainComment.getId());

			//then
			verify(mainCommentRepository).findMainCommentById(mainComment.getId());
			assertThat(updateCommentDto).usingRecursiveComparison()
				.isEqualTo(UpdateCommentDto.of(mainComment.getId(), mainComment, UpdateCommentDto.CommentType.MAIN));
		}

		@Test
		void notExistMainCommentThanThrow() throws Exception {
			//given
			//when
			//then
			assertThatThrownBy(() -> commentService.getMainCommentOnly(anyLong()))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorMessage.MAIN_COMMENT_NOT_FOUND.getMessage());
		}
	}

	@Nested
	@DisplayName("getSubCommentOnly 메서드는")
	class GetSubCommentOnlyMethod {
		@Test
		@DisplayName("정상 흐름이면 SubComment를 담은 UpdateCommentDto를 반환한다")
		void correct() throws Exception {
			//given
			MainComment mainComment = createMainComment(FullPost.builder().build(), createUser(1L));
			SubComment subComment = createSubComment(mainComment, createUser(2L));
			given(subCommentRepository.findSubCommentById(subComment.getId()))
				.willReturn(Optional.of(subComment));

			//when
			UpdateCommentDto updateCommentDto = commentService.getSubCommentOnly(subComment.getId());

			//then
			verify(subCommentRepository).findSubCommentById(subComment.getId());
			assertThat(updateCommentDto).usingRecursiveComparison()
				.isEqualTo(UpdateCommentDto.of(subComment.getId(), subComment, UpdateCommentDto.CommentType.SUB));
		}

		@Test
		void notExistSubCommentThanThrow() throws Exception {
			//given
			//when
			//then
			assertThatThrownBy(() -> commentService.getSubCommentOnly(anyLong()))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorMessage.SUB_COMMENT_NOT_FOUND.getMessage());
		}
	}

	@Nested
	@DisplayName("updateMainComment 메서드는")
	class UpdateMainCommentMethod {

		@Test
		@DisplayName("정상 흐름이면 MainComment를 업데이트한다.")
		void updateMainComment() throws Exception {
			//given
			User loginUser = createUser(1L);
			FullPost fullPost = FullPost.builder().content("내용").build();
			SimplePost simplePost = SimplePost.builder()
				.leader(loginUser)
				.fullPost(fullPost)
				.matching(Matching.builder().build())
				.build();
			ReflectionTestUtils.setField(simplePost, "id", 1L);
			MainComment mainComment = createMainComment(fullPost, loginUser);

			String updateContent = "내용 업데이트";
			UpdateCommentDto dto = UpdateCommentDto.builder().content(updateContent).build();
			given(mainCommentRepository.findMainCommentById(anyLong())).willReturn(Optional.of(mainComment));
			given(mainCommentRepository.findBySimplePostIdAsMainCommentId(anyLong()))
				.willReturn(simplePost.getId());

			//when
			long result = commentService.updateMainComment(anyLong(), dto, loginUser);

			//then
			verify(mainCommentRepository).findMainCommentById(anyLong());
			verify(mainCommentRepository).findBySimplePostIdAsMainCommentId(anyLong());
			assertThat(mainComment.getComment().getContent()).isEqualTo(updateContent);
			assertThat(mainComment.getFullPost().getSimplePost().getId()).isEqualTo(result);
		}

		@Test
		void matchedSimplePostAreNotExistThanThrow() throws Exception {
			//given
			User loginUser = createUser(1L);
			FullPost fullPost = FullPost.builder().content("내용").build();
			MainComment mainComment = createMainComment(fullPost, loginUser);

			String updateContent = "내용 업데이트";
			UpdateCommentDto dto = UpdateCommentDto.builder().content(updateContent).build();
			given(mainCommentRepository.findMainCommentById(anyLong())).willReturn(Optional.of(mainComment));
			given(mainCommentRepository.findBySimplePostIdAsMainCommentId(anyLong())).willReturn(null);

			//when
			//then
			assertThatThrownBy(() -> commentService.updateMainComment(mainComment.getId(), dto, loginUser))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorMessage.SIMPLE_POST_NOT_FOUND.getMessage());
			assertThat(mainComment.getComment().getContent()).isEqualTo(updateContent);//엔티티는 업데이트 되나 실제 DB엔 반영 X
		}

		@Nested
		@DisplayName("인자로 들어온 mainCommentId값에 해당하는 엔티티가")
		class mainCommentId {

			@Test
			@DisplayName("DB에 없을 때 에러를 반환한다.")
			void noMainCommentEntity() throws Exception {
				//given
				//when

				//then
				assertThatThrownBy(() -> commentService.updateMainComment(anyLong(), null, null))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(ErrorMessage.MAIN_COMMENT_NOT_FOUND.getMessage());
				verify(mainCommentRepository, never()).findBySimplePostIdAsMainCommentId(anyLong());
			}

			@Test
			@DisplayName("댓글 주인이 로그인 유저와 동일하지 않다면 에러를 반환한다.")
			void noCommentOwner() throws Exception {
				//given
				User loginUser = createUser(1L);
				User commentOwner = createUser(2L);
				MainComment mainComment = createMainComment(FullPost.builder().content("내용").build(), commentOwner);
				given(mainCommentRepository.findMainCommentById(anyLong())).willReturn(Optional.of(mainComment));

				//when
				//then
				assertThatThrownBy(() -> commentService.updateMainComment(anyLong(), null, loginUser))
					.isInstanceOf(InvalidAccessException.class)
					.hasMessage(ErrorMessage.NOT_COMMENT_OWNER.getMessage());
				verify(mainCommentRepository, never()).findBySimplePostIdAsMainCommentId(anyLong());
			}
		}
	}

	@Nested
	@DisplayName("updateSubComment 메서드는")
	class UpdateSubCommentMethod {

		@Test
		@DisplayName("정상 흐름이면 subComment 업데이트한다.")
		void updateSubComment() throws Exception {
			//given
			User loginUser = createUser(1L);
			FullPost fullPost = FullPost.builder().content("내용").build();
			SimplePost simplePost = SimplePost.builder()
				.leader(loginUser)
				.fullPost(fullPost)
				.matching(Matching.builder().build())
				.build();
			ReflectionTestUtils.setField(simplePost, "id", 1L);
			MainComment mainComment = createMainComment(fullPost, loginUser);
			SubComment subComment = createSubComment(mainComment, loginUser);

			String updateContent = "내용 업데이트";
			UpdateCommentDto dto = UpdateCommentDto.builder().content(updateContent).build();
			given(subCommentRepository.findSubCommentById(anyLong())).willReturn(Optional.of(subComment));
			given(subCommentRepository.findBySimplePostIdAsSubCommentId(anyLong()))
				.willReturn(simplePost.getId());

			//when
			long result = commentService.updateSubComment(anyLong(), dto, loginUser);

			//then
			verify(subCommentRepository).findSubCommentById(anyLong());
			verify(subCommentRepository).findBySimplePostIdAsSubCommentId(anyLong());
			assertThat(subComment.getComment().getContent()).isEqualTo(updateContent);
			assertThat(subComment.getMainComment().getFullPost().getSimplePost().getId()).isEqualTo(result);
		}

		@Test
		void matchedSimplePostAreNotExistThanThrow() throws Exception {
			//given
			User loginUser = createUser(1L);
			FullPost fullPost = FullPost.builder().content("내용").build();
			MainComment mainComment = createMainComment(fullPost, loginUser);
			SubComment subComment = createSubComment(mainComment, loginUser);

			String updateContent = "내용 업데이트";
			UpdateCommentDto dto = UpdateCommentDto.builder().content(updateContent).build();
			given(subCommentRepository.findSubCommentById(anyLong())).willReturn(Optional.of(subComment));
			given(subCommentRepository.findBySimplePostIdAsSubCommentId(anyLong())).willReturn(null);

			//when
			//then
			assertThatThrownBy(() -> commentService.updateSubComment(subComment.getId(), dto, loginUser))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorMessage.SIMPLE_POST_NOT_FOUND.getMessage());
			assertThat(subComment.getComment().getContent()).isEqualTo(updateContent);//엔티티는 업데이트 되나 실제 DB엔 반영 X
		}

		@Nested
		@DisplayName("인자로 들어온 subCommentId값에 해당하는 엔티티가")
		class subCommentId {

			@Test
			@DisplayName("DB에 없을 때 에러를 반환한다.")
			void noMainCommentEntity() throws Exception {
				//given
				//when
				//then
				assertThatThrownBy(() -> commentService.updateSubComment(anyLong(), null, null))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(ErrorMessage.SUB_COMMENT_NOT_FOUND.getMessage());
				verify(subCommentRepository, never()).findBySimplePostIdAsSubCommentId(anyLong());
			}

			@Test
			@DisplayName("댓글 주인이 로그인 유저와 동일하지 않다면 에러를 반환한다.")
			void noCommentOwner() throws Exception {
				//given
				User loginUser = createUser(1L);
				User commentOwner = createUser(2L);
				MainComment mainComment = createMainComment(FullPost.builder().content("내용").build(), loginUser);
				SubComment subComment = createSubComment(mainComment, commentOwner);
				given(subCommentRepository.findSubCommentById(anyLong())).willReturn(Optional.of(subComment));

				//when
				//then
				assertThatThrownBy(() -> commentService.updateSubComment(anyLong(), null, loginUser))
					.isInstanceOf(InvalidAccessException.class)
					.hasMessage(ErrorMessage.NOT_COMMENT_OWNER.getMessage());
				verify(subCommentRepository, never()).findBySimplePostIdAsSubCommentId(anyLong());
			}
		}
	}

	@Nested
	@DisplayName("deleteMainComment 메서드는")
	class DeleteMainCommentMethod {

		@Test
		@DisplayName("정상 흐름일 경우 mainComment를 삭제한다")
		void correct() throws Exception {
			//given
			User commentOwner = createUser(1L);
			MainComment mainComment = createMainComment(FullPost.builder().build(), commentOwner);
			given(mainCommentRepository.findMainCommentById(anyLong())).willReturn(Optional.of(mainComment));

			//when
			commentService.deleteMainComment(anyLong(), commentOwner);

			//then
			verify(mainCommentRepository).findMainCommentById(anyLong());
			verify(mainCommentRepository).delete(mainComment);
		}

		@Nested
		@DisplayName("인자로 들어온 mainCommentId가")
		class MainCommentIdParam {

			@Test
			@DisplayName("대응되는 엔티티가 없을 경우 예외를 반환한다")
			void entityNotExistThanThrow() throws Exception {
				//given
				//when
				//then
				assertThatThrownBy(() -> commentService.deleteMainComment(1L, any(User.class)))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(ErrorMessage.MAIN_COMMENT_NOT_FOUND.getMessage());
				verify(mainCommentRepository, never()).delete(any(MainComment.class));
			}

			@Test
			@DisplayName("댓글 주인이 로그인 유저와 동일하지 않다면 에러를 반환한다.")
			void notCommentOwnerThanThrow() throws Exception {
				//given
				User loginUser = createUser(1L);
				User commentOwner = createUser(2L);
				MainComment mainComment = createMainComment(FullPost.builder().content("내용").build(), commentOwner);
				given(mainCommentRepository.findMainCommentById(anyLong())).willReturn(Optional.of(mainComment));

				//when
				//then
				assertThatThrownBy(() -> commentService.deleteMainComment(anyLong(), loginUser))
					.isInstanceOf(InvalidAccessException.class)
					.hasMessage(ErrorMessage.NOT_COMMENT_OWNER.getMessage());
				verify(mainCommentRepository, never()).delete(any(MainComment.class));
			}
		}
	}

	@Nested
	@DisplayName("deleteSubComment 메서드는")
	class DeleteSubCommentMethod {

		@Test
		@DisplayName("정상 흐름일 경우 subComment를 삭제한다")
		void correct() throws Exception {
			//given
			User commentOwner = createUser(1L);
			MainComment mainComment = createMainComment(FullPost.builder().build(), commentOwner);
			SubComment subComment = createSubComment(mainComment, commentOwner);
			given(subCommentRepository.findSubCommentById(anyLong())).willReturn(Optional.of(subComment));

			//when
			commentService.deleteSubComment(anyLong(), commentOwner);

			//then
			verify(subCommentRepository).findSubCommentById(anyLong());
			verify(subCommentRepository).delete(subComment);
		}

		@Nested
		@DisplayName("인자로 들어온 subCommentId가")
		class MainCommentIdParam {

			@Test
			@DisplayName("대응되는 엔티티가 없을 경우 예외를 반환한다")
			void entityNotExistThanThrow() throws Exception {
				//given
				//when
				//then
				assertThatThrownBy(() -> commentService.deleteSubComment(1L, any(User.class)))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(ErrorMessage.SUB_COMMENT_NOT_FOUND.getMessage());
				verify(subCommentRepository, never()).delete(any(SubComment.class));
			}

			@Test
			@DisplayName("댓글 주인이 로그인 유저와 동일하지 않다면 에러를 반환한다.")
			void notCommentOwnerThanThrow() throws Exception {
				//given
				User loginUser = createUser(1L);
				User commentOwner = createUser(2L);
				MainComment mainComment = createMainComment(FullPost.builder().content("내용").build(), loginUser);
				SubComment subComment = createSubComment(mainComment, commentOwner);
				given(subCommentRepository.findSubCommentById(anyLong())).willReturn(Optional.of(subComment));

				//when
				//then
				assertThatThrownBy(() -> commentService.deleteSubComment(anyLong(), loginUser))
					.isInstanceOf(InvalidAccessException.class)
					.hasMessage(ErrorMessage.NOT_COMMENT_OWNER.getMessage());
				verify(subCommentRepository, never()).delete(any(SubComment.class));
			}
		}
	}
}