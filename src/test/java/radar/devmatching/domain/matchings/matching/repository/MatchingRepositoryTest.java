package radar.devmatching.domain.matchings.matching.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.repository.MatchingUserRepository;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.repository.SimplePostRepository;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;

@DataJpaTest
@DisplayName("MatchingRepository의")
class MatchingRepositoryTest {

	@Autowired
	UserRepository userRepository;
	@Autowired
	SimplePostRepository simplePostRepository;
	@Autowired
	MatchingUserRepository matchingUserRepository;
	@Autowired
	MatchingRepository matchingRepository;

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

	private SimplePost createSimplePost(User user, FullPost fullPost, Matching matching) {
		return SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.leader(user)
			.fullPost(fullPost)
			.matching(matching)
			.build();
	}

	@Test
	@DisplayName("findByMatchingIdAndUserId 메서드는 userId와 matchingId에 해당하는 Matching을 반환한다.")
	void findByMatchingIdAndUserId() {
		//given
		User user = createUser();
		userRepository.save(user);
		Matching matching = Matching.builder().build();
		SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
		simplePostRepository.save(simplePost);
		MatchingUser matchingUser = MatchingUser.builder().user(user).matching(matching).build();
		matchingUserRepository.save(matchingUser);
		//when
		Optional<Matching> findMatching = matchingRepository.findByMatchingIdAndUserId(matching.getId(),
			user.getId());
		//then
		assertThat(findMatching.get()).usingRecursiveComparison().isEqualTo(matching);

	}

}