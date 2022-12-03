package radar.devmatching.domain.user.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.repository.MatchingRepository;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.repository.MatchingUserRepository;
import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.user.entity.User;

@DataJpaTest
@DisplayName("UserRepository에서")
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SimplePostRepository simplePostRepository;
	@Autowired
	private MatchingUserRepository matchingUserRepository;
	@Autowired
	private MatchingRepository matchingRepository;
	@Autowired
	private ApplyRepository applyRepository;

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
			.leader(user)
			.matching(Matching.builder().build())
			.fullPost(fullPost)
			.build();
	}

	@Nested
	@DisplayName("delete 메서드로")
	class Delete_Method {

		@Nested
		@DisplayName("User를 삭제하면")
		class Delete_User {

			@Test
			@DisplayName("SimplePost도 같이 삭제된다.")
			void with_delete_simplePost() throws Exception {
				//given
				User user = createUser();
				userRepository.save(user);
				SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build());
				simplePostRepository.save(simplePost);

				//when
				userRepository.delete(user);

				//then
				assertThat(simplePostRepository.findById(simplePost.getId()).isEmpty()).isTrue();
			}

			@Test
			@DisplayName("MatchingUser도 같이 삭제된다.")
			public void with_delete_matchingUser() throws Exception {
				//given
				User user = createUser();
				userRepository.save(user);
				SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build());
				simplePostRepository.save(simplePost);
				MatchingUser matchingUser = MatchingUser.builder()
					.user(user)
					.matching(simplePost.getMatching())
					.build();
				matchingUserRepository.save(matchingUser);

				//when
				userRepository.delete(user);

				//then
				assertThat(matchingUserRepository.findById(matchingUser.getId()).isEmpty()).isTrue();
			}

			@Test
			@DisplayName("Apply도 같이 삭제된다.")
			public void with_delete_apply() throws Exception {
				//given
				User user = createUser();
				userRepository.save(user);
				SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build());
				simplePostRepository.save(simplePost);

				Apply apply = Apply.builder().applyUser(user).applySimplePost(simplePost).build();
				applyRepository.save(apply);

				//when
				userRepository.delete(user);

				//then
				assertThat(applyRepository.findById(apply.getId()).isEmpty()).isTrue();
			}
		}
	}
}
