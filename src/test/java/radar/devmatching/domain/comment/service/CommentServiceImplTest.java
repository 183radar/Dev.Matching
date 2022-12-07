package radar.devmatching.domain.comment.service;

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
import org.springframework.test.util.ReflectionTestUtils;

import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.InvalidAccessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.repository.MainCommentRepository;
import radar.devmatching.domain.comment.repository.SubCommentRepository;
import radar.devmatching.domain.comment.service.dto.UpdateCommentDto;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService 클래스의")
class CommentServiceImplTest {

	// private final static User loginUser = createUser();

	@Mock
	SimplePostRepository simplePostRepository;
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

	@BeforeEach
	void setUp() {
		this.commentService = new CommentServiceImpl(simplePostRepository, mainCommentRepository, subCommentRepository);
	}

	private MainComment createMainComment(FullPost fullPost, User user) {
		return MainComment.builder()
			.fullPost(fullPost)
			.comment(createComment(user))
			.build();
	}

	private Comment createComment(User user) {
		return Comment.builder()
			.user(user)
			.content("내용")
			.build();
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

			//when
			long result = commentService.updateMainComment(anyLong(), dto, loginUser);

			//then
			assertThat(mainComment.getComment().getContent()).isEqualTo(updateContent);
			assertThat(mainComment.getFullPost().getSimplePost().getId()).isEqualTo(result);
		}

		@Nested
		@DisplayName("인자로 들어온 mainCommentId값에 해당하는 엔티티가")
		class mainCommentId {

			@Test
			@DisplayName("DB에 없을 때 에러를 반환한다.")
			void noMainCommentEntity() throws Exception {
				//given
				long notExistMainCommentId = 1L;
				//when

				//then
				assertThatThrownBy(() -> commentService.updateMainComment(
					notExistMainCommentId, null, null))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(ErrorMessage.MAIN_COMMENT_NOT_FOUND.getMessage());
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
			}
		}
	}

}