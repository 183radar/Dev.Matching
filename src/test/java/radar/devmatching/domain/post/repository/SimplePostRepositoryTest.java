package radar.devmatching.domain.post.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.repository.MatchingRepository;
import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;

@DataJpaTest
@DisplayName("SimplePostRepository의")
class SimplePostRepositoryTest {

	@Autowired
	SimplePostRepository simplePostRepository;
	@Autowired
	FullPostRepository fullPostRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	MatchingRepository matchingRepository;
	@Autowired
	ApplyRepository applyRepository;

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

	private SimplePost createSimplePost(User user, FullPost fullPost) {
		return SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.user(user)
			.fullPost(fullPost)
			.build();
	}

	@Test
	@DisplayName("findMyPostByUserId메서드는 유저Id를 받으면 유저가 생성한 게시글들을 반환한다.")
	void findMyPostByUserIdTest() throws Exception {
		//given
		User user = createUser();
		userRepository.save(user);
		simplePostRepository.save(createSimplePost(user, FullPost.builder().content("내용1").build()));
		simplePostRepository.save(createSimplePost(user, FullPost.builder().content("내용2").build()));

		//when
		List<SimplePost> simplePosts = simplePostRepository.findMyPostByUserId(user.getId());

		//then
		assertThat(simplePosts.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("findApplicationPosts메서드는 유저ID를 받으면 해당 유저가 신청한 게시글들을 반환한다.")
	void findApplicationPostsTest() throws Exception {
		//given
		User user = createUser();
		userRepository.save(user);
		SimplePost simplePost = createSimplePost(user, FullPost.builder().content("내용1").build());
		simplePostRepository.save(simplePost);
		Apply apply1 = Apply.builder()
			.applyUser(user)
			.applySimplePost(simplePost)
			.build();
		Apply apply2 = Apply.builder()
			.applyUser(user)
			.applySimplePost(simplePost)
			.build();
		applyRepository.save(apply1);
		applyRepository.save(apply2);

		//when
		List<SimplePost> applicationSimplePosts = simplePostRepository.findApplicationPosts(user.getId());

		//then
		assertThat(applicationSimplePosts.containsAll(List.of(apply1, apply2)));

	}

	@Nested
	@DisplayName("save 메서드는")
	class SaveMethodIs {

		@Nested
		@DisplayName("SimplePost 엔티티를 저장하면")
		class SaveSimplePost {

			@Test
			@DisplayName("FullPost 엔티티까지 저장다.")
			void saveFullPostTogether() throws Exception {
				//given
				SimplePost simplePost = createUserAndSimplePost();

				//when
				SimplePost savedSimplePost = simplePostRepository.save(simplePost);
				Optional<FullPost> findFullPost = fullPostRepository.findById(savedSimplePost.getFullPost().getId());

				//then
				assertThat(findFullPost.isPresent()).isTrue();
			}

			@Test
			@DisplayName("Matcing 엔티티까지 저장한다.")
			void saveMatchingTogether() throws Exception {
				//given
				SimplePost simplePost = createUserAndSimplePost();

				//when
				SimplePost savedSimplePost = simplePostRepository.save(simplePost);
				Optional<Matching> findMatching = matchingRepository.findById(savedSimplePost.getMatching().getId());

				//then
				assertThat(findMatching.isPresent()).isTrue();
			}

			private SimplePost createUserAndSimplePost() {
				User user = createUser();
				userRepository.save(user);
				SimplePost simplePost = createSimplePost(user, FullPost.builder().content("내용").build());
				return simplePost;
			}
		}
	}

	@Nested
	@DisplayName("delete 메서드는")
	class DeleteMethodIs {

		@Nested
		@DisplayName("SimplePost 엔티티를 삭제하면")
		class DeleteSimplePost {

			@Test
			@DisplayName("SimplePost가 참조하는 FullPost 엔티티도 삭제한다")
			void deleteFullPostTogether() throws Exception {
				//given
				SimplePost simplePost = createAndSaveUserAndSimplePost();

				//when
				simplePostRepository.delete(simplePost);

				//then
				assertThat(fullPostRepository.findById(simplePost.getFullPost().getId()).isEmpty()).isTrue();
			}

			@Test
			@DisplayName("SimplePost가 참조하는 Matching 엔티티도 삭제한다")
			void deleteMatchingTogether() throws Exception {
				//given
				SimplePost simplePost = createAndSaveUserAndSimplePost();

				//when
				simplePostRepository.delete(simplePost);

				//then
				assertThat(matchingRepository.findById(simplePost.getMatching().getId()).isEmpty()).isTrue();
			}

			@Test
			@DisplayName("SimplePost가 참조하는 ApplyList 엔티티들도 삭제한다")
			void deleteApplyListTogether() throws Exception {
				//given
				User user = createUser();
				userRepository.save(user);
				FullPost fullPost = FullPost.builder().content("내용").build();
				SimplePost simplePost = simplePostRepository.save(createSimplePost(user, fullPost));
				Apply apply1 = Apply.builder()
					.applyUser(user)
					.applySimplePost(simplePost)
					.build();
				Apply apply2 = Apply.builder()
					.applyUser(user)
					.applySimplePost(simplePost)
					.build();
				apply1 = applyRepository.save(apply1);
				apply2 = applyRepository.save(apply2);

				//when
				simplePostRepository.delete(simplePost);

				//then
				assertThat(applyRepository.findById(apply1.getId()).isEmpty()).isTrue();
				assertThat(applyRepository.findById(apply2.getId()).isEmpty()).isTrue();
			}

			private SimplePost createAndSaveUserAndSimplePost() {
				User user = createUser();
				userRepository.save(user);
				return simplePostRepository.save(createSimplePost(user, FullPost.builder().content("내용").build()));
			}

		}
	}
}