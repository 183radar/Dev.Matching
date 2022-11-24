package radar.devmatching.domain.comment.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.entity.SubComment;
import radar.devmatching.domain.post.entity.FullPost;

@DataJpaTest
@DisplayName("SubCommentRepository의")
class SubCommentRepositoryTest {

	@Autowired
	SubCommentRepository subCommentRepository;

	@Autowired
	CommentRepository commentRepository;

	private Comment createComment() {
		return Comment.builder().
			user(null)
			.content("내용")
			.build();
	}

	private MainComment createMainComment() {
		return MainComment.builder()
			.comment(createComment())
			.fullPost(FullPost.builder().content("내용").build())
			.build();
	}

	@Nested
	@DisplayName("save 메서드는")
	class SaveMethodIs {

		@Nested
		@DisplayName("SubComment 엔티티를 저장하면")
		class SaveSubComment {

			@Test
			@DisplayName("Comment 엔티티까지 저장된다.")
			void saveCommentTogether() throws Exception {
				//given
				Comment comment = createComment();
				SubComment mainComment = SubComment.builder()
					.mainComment(createMainComment())
					.comment(comment)
					.build();

				//when
				subCommentRepository.save(mainComment);

				//then
				assertThat(commentRepository.findById(comment.getId()).isPresent()).isTrue();
			}
		}
	}

	@Nested
	@DisplayName("delete 메서드는")
	class DeleteMethodIs {

		@Nested
		@DisplayName("SubComment 엔티티를 삭제하면")
		class DeleteSubComment {

			@Test
			@DisplayName("SubComment가 참조하는 Comment 엔티티도 삭제한다")
			void deleteCommentTogether() throws Exception {
				//given
				Comment comment = createComment();
				SubComment mainComment = SubComment.builder()
					.mainComment(createMainComment())
					.comment(comment)
					.build();
				subCommentRepository.save(mainComment);

				//when
				subCommentRepository.delete(mainComment);

				//then
				assertThat(commentRepository.findById(comment.getId()).isEmpty()).isTrue();
			}
		}
	}
}