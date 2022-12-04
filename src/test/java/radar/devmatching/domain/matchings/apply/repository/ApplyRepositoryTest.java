package radar.devmatching.domain.matchings.apply.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.entity.FullPost;
import radar.devmatching.domain.post.entity.PostCategory;
import radar.devmatching.domain.post.entity.Region;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;

@DataJpaTest
@DisplayName("ApplyRepository의")
class ApplyRepositoryTest {

	@Autowired
	private ApplyRepository applyRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SimplePostRepository simplePostRepository;

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
	@DisplayName("findAllByApplySimplePostId 메서드에서 simplePostId를 가지는 모든 apply 반환한다.")
	public void findAllByApplySimplePostIdMethod() {
		//given
		User user = createUser();
		userRepository.save(user);
		Matching matching = Matching.builder().build();
		SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
		simplePostRepository.save(simplePost);
		Apply apply = Apply.builder()
			.applyUser(user)
			.applySimplePost(simplePost)
			.build();
		applyRepository.save(apply);
		//when
		List<Apply> findSimplePost = applyRepository.findAllByApplySimplePostId(simplePost.getId());
		//then
		assertThat(findSimplePost).contains(apply);
	}

	@Test
	@DisplayName("findApplyBySimplePostIdAndUserId는 simplePostId와 userId를 가지는 Apply 엔티티를 반환한다.")
	public void findApplyBySimplePostIdAndUserIdMethod() {
		//given
		User user = createUser();
		userRepository.save(user);
		Matching matching = Matching.builder().build();
		SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
		simplePostRepository.save(simplePost);
		Apply apply = Apply.builder()
			.applyUser(user)
			.applySimplePost(simplePost)
			.build();
		applyRepository.save(apply);
		//when
		Apply findApply = applyRepository.findByApplySimplePostIdAndApplyUserId(
			simplePost.getId(), user.getId()).get();
		//then
		assertThat(findApply).usingRecursiveComparison().isEqualTo(apply);
	}

}