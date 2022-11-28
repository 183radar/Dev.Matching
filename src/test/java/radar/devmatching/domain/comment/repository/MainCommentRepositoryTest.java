package radar.devmatching.domain.comment.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.entity.SubComment;
import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.repository.FullPostRepository;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;

@DataJpaTest
@DisplayName("MainCommentRepository의")
class MainCommentRepositoryTest {

	@Autowired
	EntityManager em;
	@Autowired
	UserRepository userRepository;
	@Autowired
	FullPostRepository fullPostRepository;
	@Autowired
	MainCommentRepository mainCommentRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	SubCommentRepository subCommentRepository;

	private User createUser() {
		return User.builder()
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("schoolName")
			.githubUrl("githubUrl")
			.introduce("introduce")
			.build();
	}

	private Comment createComment() {
		return Comment.builder().
			user(userRepository.save(createUser()))
			.content("내용")
			.build();
	}

	@Nested
	@DisplayName("save 메서드는")
	class SaveMethodIs {

		@Nested
		@DisplayName("MainComment 엔티티를 저장하면")
		class SaveMainComment {

			@Test
			@DisplayName("Comment 엔티티까지 저장된다.")
			void saveCommentTogether() throws Exception {
				//given
				Comment comment = createComment();
				MainComment mainComment = MainComment.builder()
					.fullPost(FullPost.builder().content("내용").build())
					.comment(comment)
					.build();

				//when
				mainCommentRepository.save(mainComment);

				//then
				assertThat(commentRepository.findById(comment.getId()).isPresent()).isTrue();
			}
		}
	}

	@Nested
	@DisplayName("delete 메서드는")
	class DeleteMethodIs {

		@Nested
		@DisplayName("MainComment 엔티티를 삭제하면")
		class DeleteMainComment {

			@Test
			@DisplayName("MainComment가 참조하는 Comment 엔티티도 삭제한다")
			void deleteCommentTogether() throws Exception {
				//given
				Comment comment = createComment();
				MainComment mainComment = MainComment.builder()
					.fullPost(FullPost.builder().content("내용").build())
					.comment(comment)
					.build();
				mainCommentRepository.save(mainComment);

				//when
				mainCommentRepository.delete(mainComment);

				//then
				assertThat(commentRepository.findById(comment.getId()).isEmpty()).isTrue();
			}

			@Test
			@DisplayName("MainComment가 참조하는 SubComment 엔티티들도 삭제한다")
			void deleteSubCommentsTogether() throws Exception {
				//given
				MainComment mainComment = MainComment.builder()
					.fullPost(FullPost.builder().content("내용").build())
					.comment(createComment())
					.build();
				SubComment subComment1 = SubComment.builder()
					.mainComment(mainComment)
					.comment(createComment())
					.build();
				SubComment subComment2 = SubComment.builder()
					.mainComment(mainComment)
					.comment(createComment())
					.build();
				mainCommentRepository.save(mainComment);
				subCommentRepository.save(subComment1);
				subCommentRepository.save(subComment2);

				//when
				mainCommentRepository.delete(mainComment);

				//then
				assertThat(subCommentRepository.findById(subComment1.getId()).isEmpty()).isTrue();
				assertThat(subCommentRepository.findById(subComment2.getId()).isEmpty()).isTrue();
			}
		}
	}

	@Nested
	@DisplayName("getAllComments 메서드는")
	class getAllCommentsMethodIs {

		@Nested
		@DisplayName("정상 흐름이면")
		class successCondition {

			@Test
			@DisplayName("Comment, SubComment가 패치조인된 MainComment들을 반환한다.")
			void returnAllComments() throws Exception {
				//given
				FullPost fullPost = FullPost.builder().content("내용").build();
				fullPostRepository.save(fullPost);
				MainComment mainComment = MainComment.builder()
					.fullPost(fullPost)
					.comment(createComment())
					.build();
				mainCommentRepository.save(mainComment);
				subCommentRepository.save(SubComment.builder()
					.mainComment(mainComment)
					.comment(createComment())
					.build());
				// 초기화를 안 하면 영속성 컨택스트의 1차 캐시에서 데이터를 가져오기에 초기화함
				em.flush();
				em.clear();

				//when
				List<MainComment> allComments = mainCommentRepository.getAllComments(fullPost.getId());

				//then
				PersistenceUnitUtil persistenceUnitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
				assertThat(persistenceUnitUtil.isLoaded(allComments.get(0).getComment())).isTrue();
				assertThat(persistenceUnitUtil.isLoaded(allComments.get(0).getSubComments().get(0))).isTrue();
			}
		}
	}

}