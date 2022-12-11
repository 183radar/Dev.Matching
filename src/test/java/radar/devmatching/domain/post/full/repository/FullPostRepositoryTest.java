package radar.devmatching.domain.post.full.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.repository.MainCommentRepository;
import radar.devmatching.domain.post.full.entity.FullPost;

@DataJpaTest
@DisplayName("FullPostRepository의")
class FullPostRepositoryTest {

	@Autowired
	FullPostRepository fullPostRepository;

	@Autowired
	MainCommentRepository mainCommentRepository;

	private MainComment createMainComment(FullPost fullPost) {
		return MainComment.builder()
			.fullPost(fullPost)
			.comment(Comment.builder().content("내용").user(null).build())
			.build();
	}

	@Nested
	@DisplayName("delete 메서드는")
	class DeleteMethodIs {

		@Nested
		@DisplayName("FullPost 엔티티를 삭제하면")
		class DeleteFullPost {

			@Test
			@DisplayName("FullPost가 참조하는 MainComment들도 삭제된다.")
			void deleteMainComments() throws Exception {
				//given
				FullPost fullPost = FullPost.builder()
					.content("내용")
					.build();
				fullPostRepository.save(fullPost);
				MainComment mainComment1 = createMainComment(fullPost);
				MainComment mainComment2 = createMainComment(fullPost);
				mainComment1 = mainCommentRepository.save(mainComment1);
				mainComment2 = mainCommentRepository.save(mainComment2);

				//when
				fullPostRepository.delete(fullPost);

				//then
				assertThat(mainCommentRepository.findById(mainComment1.getId()).isEmpty()).isTrue();
				assertThat(mainCommentRepository.findById(mainComment2.getId()).isEmpty()).isTrue();
			}
		}
	}

}