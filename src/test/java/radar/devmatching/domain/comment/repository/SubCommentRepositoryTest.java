package radar.devmatching.domain.comment.repository;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.entity.SubComment;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.full.repository.FullPostRepository;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.repository.SimplePostRepository;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;

@Slf4j
@DataJpaTest
@DisplayName("SubCommentRepository의")
class SubCommentRepositoryTest {

	@Autowired
	EntityManager em;
	@Autowired
	UserRepository userRepository;
	@Autowired
	SimplePostRepository simplePostRepository;
	@Autowired
	FullPostRepository fullPostRepository;
	@Autowired
	MainCommentRepository mainCommentRepository;
	@Autowired
	SubCommentRepository subCommentRepository;
	@Autowired
	CommentRepository commentRepository;

	private User createUser() {
		return userRepository.save(User.builder()
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("schoolName")
			.githubUrl("githubUrl")
			.introduce("introduce")
			.build());
	}

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

	@Test
	@DisplayName("findSubCommentById 메서드는 subCommentId값을 받으면 SubComment에 Comment를 패치하여 가져온다.")
	void findSubCommentByIdTest() throws Exception {
		//given
		PersistenceUnitUtil persistenceUnitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
		FullPost fullPost = FullPost.builder().content("내용").build();
		fullPostRepository.save(fullPost);
		MainComment mainComment = MainComment.builder()
			.fullPost(fullPost)
			.comment(createComment())
			.build();
		mainCommentRepository.save(mainComment);
		SubComment subComment = SubComment.builder()
			.mainComment(mainComment)
			.comment(createComment())
			.build();
		subCommentRepository.save(subComment);
		em.flush();
		em.clear();

		//when
		SubComment findSubComment = subCommentRepository.findSubCommentById(subComment.getId()).get();

		//then
		assertThat(persistenceUnitUtil.isLoaded(findSubComment.getComment())).isTrue();
	}

	@Nested
	@DisplayName("findBySimplePostIdAsSubCommentId 메서드는")
	class findBySimplePostIdAsSubCommentIdMethod {

		@Test
		@DisplayName("정상 흐름이면 subCommentId에 해당하는 댓글이 포함된 게시글의 simplePostId값을 가져온다")
		void correctThanReturnSimplePostId() throws Exception {
			//given
			User user = userRepository.save(createUser());
			FullPost fullPost = FullPost.builder().content("내용").build();
			SimplePost simplePost = SimplePost.builder()
				.leader(user)
				.matching(Matching.builder().build())
				.fullPost(fullPost)
				.build();
			simplePostRepository.save(simplePost);
			SimplePost simplePost2 = SimplePost.builder()
				.leader(user)
				.matching(Matching.builder().build())
				.fullPost(FullPost.builder().build())
				.build();
			simplePostRepository.save(simplePost2);
			MainComment mainComment = MainComment.builder()
				.fullPost(fullPost)
				.comment(createComment())
				.build();
			mainCommentRepository.save(mainComment);
			SubComment subComment = SubComment.builder()
				.mainComment(mainComment)
				.comment(createComment())
				.build();
			subCommentRepository.save(subComment);

			//when
			Long findSimplePostId = subCommentRepository.findBySimplePostIdAsSubCommentId(subComment.getId());

			//then
			assertThat(findSimplePostId).isNotNull();
			assertThat(findSimplePostId).isEqualTo(simplePost.getId());
		}
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