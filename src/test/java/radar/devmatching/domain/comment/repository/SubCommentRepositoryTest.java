package radar.devmatching.domain.comment.repository;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.comment.entity.SubComment;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.FullPostRepository;
import radar.devmatching.domain.post.repository.SimplePostRepository;
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
	@DisplayName("findSubCommentById 메서드는 subCommentId값을 받으면 Comment MainComment, FullPost, SimplePost를 패치하여 가져온다.")
	void findSubCommentByIdTest() throws Exception {
		//given
		PersistenceUnitUtil persistenceUnitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
		FullPost fullPost = FullPost.builder().content("내용").build();
		fullPostRepository.save(fullPost);
		SimplePost simplePost = SimplePost.builder()
			.matching(Matching.builder().build())
			.fullPost(fullPost)
			.leader(createUser()).build();
		simplePostRepository.save(simplePost);
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
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		SubComment findSubComment = subCommentRepository.findSubCommentById(subComment.getId()).get();
		Long id = subCommentRepository.findBySimplePostIdAsSubCommentId(subComment.getId());
		System.out.println("id = " + id);
		stopWatch.stop();
		log.info("findSubCommentById time: {}", stopWatch.getTotalTimeMillis());

		//then
		assertThat(persistenceUnitUtil.isLoaded(findSubComment.getComment())).isTrue();
		assertThat(persistenceUnitUtil.isLoaded(findSubComment.getMainComment())).isTrue();
		assertThat(persistenceUnitUtil.isLoaded(findSubComment.getMainComment().getFullPost())).isTrue();
		assertThat(
			persistenceUnitUtil.isLoaded(findSubComment.getMainComment().getFullPost().getSimplePost())).isTrue();
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