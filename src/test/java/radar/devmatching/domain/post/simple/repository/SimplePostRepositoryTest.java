package radar.devmatching.domain.post.simple.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import radar.devmatching.common.exception.InvalidParamException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matching.repository.MatchingRepository;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.full.repository.FullPostRepository;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.PostState;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.dto.MainPostDto;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;

@DataJpaTest
@DisplayName("SimplePostRepository의")
class SimplePostRepositoryTest {

	@Autowired
	EntityManager em;
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
			.leader(user)
			.matching(Matching.builder().build())
			.fullPost(fullPost)
			.build();
	}

	private SimplePost createSimplePostWithCategory(User user, PostCategory category) {
		return SimplePost.builder()
			.title("게시글 제목")
			.category(category)
			.region(Region.BUSAN)
			.userNum(1)
			.leader(user)
			.matching(Matching.builder().build())
			.fullPost(FullPost.builder().build())
			.build();
	}

	private SimplePost createSimplePostWithRegion(User user, Region region) {
		return SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(region)
			.userNum(1)
			.leader(user)
			.matching(Matching.builder().build())
			.fullPost(FullPost.builder().build())
			.build();
	}

	private SimplePost createSimplePostWithTitle(User user, String title) {
		return SimplePost.builder()
			.title(title)
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.leader(user)
			.matching(Matching.builder().build())
			.fullPost(FullPost.builder().build())
			.build();
	}

	@Test
	@DisplayName("findMyPostsByLeaderIdOrderByCreateDateDesc메서드는 유저Id를 받으면 유저가 생성한 게시글들을 최신순으로 반환한다.")
	void findMyPostsByLeaderIdOrderByCreateDateDescTest() throws Exception {
		//given
		User user = createUser();
		userRepository.save(user);
		SimplePost simplePost1 = createSimplePost(user, FullPost.builder().content("내용1").build());
		simplePostRepository.save(simplePost1);
		SimplePost simplePost2 = createSimplePost(user, FullPost.builder().content("내용2").build());
		simplePostRepository.save(simplePost2);

		//when
		List<SimplePost> simplePosts = simplePostRepository.findMyPostsByLeaderIdOrderByCreateDateDesc(user.getId());

		//then
		assertThat(simplePosts.get(0).getFullPost().getContent()).isEqualTo("내용2");
		assertThat(simplePosts.get(1).getFullPost().getContent()).isEqualTo("내용1");
	}

	@Test
	@DisplayName("findApplicationPosts메서드는 유저ID를 받으면 해당 유저가 신청한 게시글들을 최신순으로 반환한다.")
	void findApplicationPostsTest() throws Exception {
		//given
		User user = createUser();
		userRepository.save(user);
		SimplePost simplePost1 = createSimplePost(user, FullPost.builder().content("내용1").build());
		SimplePost simplePost2 = createSimplePost(user, FullPost.builder().content("내용2").build());
		simplePostRepository.save(simplePost1);
		simplePostRepository.save(simplePost2);
		Apply apply1 = Apply.builder()
			.applyUser(user)
			.applySimplePost(simplePost1)
			.build();
		Apply apply2 = Apply.builder()
			.applyUser(user)
			.applySimplePost(simplePost2)
			.build();
		applyRepository.save(apply1);
		applyRepository.save(apply2);

		//when
		List<SimplePost> applicationSimplePosts = simplePostRepository.findApplicationPosts(user.getId());

		//then
		assertThat(applicationSimplePosts.get(0).getFullPost().getContent()).isEqualTo("내용2");
		assertThat(applicationSimplePosts.get(1).getFullPost().getContent()).isEqualTo("내용1");
	}

	@Test
	@DisplayName("findPostById메서드는 userId를 받으면 SimplePost에 FullPost까지 패치하여 가져온다.")
	void findPostByIdTest() throws Exception {
		//given
		FullPost fullPost = FullPost.builder().content("내용").build();
		User leader = userRepository.save(createUser());
		SimplePost simplePost = createSimplePost(leader, fullPost);
		simplePostRepository.save(simplePost);
		em.flush();
		em.clear();

		//when
		Optional<SimplePost> findPost = simplePostRepository.findPostById(simplePost.getId());

		//then
		assertThat(em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(
			findPost.get().getFullPost()
		)).isTrue();
	}

	@Test
	@DisplayName("findByPostStateOrderByCreateDateDesc 메서드는 PostState 상태에 따라 게시글들을 최신순으로 반환한다")
	void findByPostStateOrderByCreateDateDescTest() throws Exception {
		//given
		User user = userRepository.save(createUser());
		SimplePost closedPost = createSimplePost(user, FullPost.builder().content("내용1").build());
		closedPost.closePost();
		simplePostRepository.save(closedPost);
		simplePostRepository.save(createSimplePost(user, FullPost.builder().content("내용1").build()));
		simplePostRepository.save(createSimplePost(user, FullPost.builder().content("내용2").build()));

		//when
		List<SimplePost> findSimplePosts = simplePostRepository.findByPostStateOrderByCreateDateDesc(
			PostState.RECRUITING);

		//then
		assertThat(findSimplePosts.get(0).getFullPost().getContent()).isEqualTo("내용2");
		assertThat(findSimplePosts.get(1).getFullPost().getContent()).isEqualTo("내용1");
	}

	@Nested
	@DisplayName("findByCategoryAndPostStateOrderByCreateDateDesc 메서드는")
	class findByCategoryAndPostStateMethod {

		@Nested
		@DisplayName("PostCategory를 인자로 받으면")
		class inputPostCategoryThan {

			@Test
			@DisplayName("Category에 해당하는 모집중인 게시글이 없을 경우 빈 리스트를 반환한다.")
			void returnEmptyListIfCategoryNotEqualAnything() throws Exception {
				//given
				User user = userRepository.save(createUser());
				simplePostRepository.save(createSimplePost(user, FullPost.builder().content("내용").build()));

				//when
				List<SimplePost> findSimplePosts = simplePostRepository.findByCategoryAndPostStateOrderByCreateDateDesc(
					PostCategory.MOGAKKO, PostState.RECRUITING);

				//then
				assertThat(findSimplePosts.isEmpty()).isTrue();
			}

			@Test
			@DisplayName("Category와 일치하는 모집중인 게시글을 최신순으로 반환한다")
			void returnSimplePostsWhenEqualsCategory() throws Exception {
				//given
				User user = userRepository.save(createUser());
				simplePostRepository.save(createSimplePost(user, FullPost.builder().content("내용1").build()));
				simplePostRepository.save(createSimplePost(user, FullPost.builder().content("내용2").build()));

				//when
				List<SimplePost> findSimplePosts = simplePostRepository.findByCategoryAndPostStateOrderByCreateDateDesc(
					PostCategory.PROJECT, PostState.RECRUITING);

				//then
				assertThat(findSimplePosts.get(0).getFullPost().getContent()).isEqualTo("내용2");
				assertThat(findSimplePosts.get(1).getFullPost().getContent()).isEqualTo("내용1");
			}
		}
	}

	@Nested
	@DisplayName("findRecruitingPostBySearchCondition 메서드는")
	class findRecruitingPostBySearchConditionMethod {

		@Test
		@DisplayName("모집 종료된 게시글들은 게시글에 포함하지 않는다")
		void endPostAreNotCollect() throws Exception {
			//given
			User user = userRepository.save(createUser());
			SimplePost simplePost = createSimplePost(user, FullPost.builder().content("내용").build());
			simplePost.closePost();
			simplePostRepository.save(simplePost);
			MainPostDto mainPostDto = MainPostDto.builder().build();

			//when
			List<SimplePost> findSimplePosts = simplePostRepository.findRecruitingPostBySearchCondition(
				"ALL", mainPostDto);

			//then
			assertThat(findSimplePosts.isEmpty()).isTrue();
		}

		@Test
		@DisplayName("게시글들을 최신순으로 정렬한다.")
		void orderByCreatedDateDesc() throws Exception {
			//given
			User user = userRepository.save(createUser());
			simplePostRepository.save(createSimplePost(user, FullPost.builder().content("내용1").build()));
			simplePostRepository.save(createSimplePost(user, FullPost.builder().content("내용2").build()));
			MainPostDto mainPostDto = MainPostDto.builder().build();

			//when
			List<SimplePost> findSimplePosts = simplePostRepository.findRecruitingPostBySearchCondition(
				"ALL", mainPostDto);

			//then
			assertThat(findSimplePosts.get(0).getFullPost().getContent()).isEqualTo("내용2");
			assertThat(findSimplePosts.get(1).getFullPost().getContent()).isEqualTo("내용1");
		}

		@Nested
		@DisplayName("카테고리 인자가")
		class CategoryParameterIs {

			@Test
			@DisplayName("유효하지 않다면 예외를 내보낸다")
			void isIncorrectThanThrowException() throws Exception {
				//given
				User user = userRepository.save(createUser());
				simplePostRepository.save(createSimplePost(user, FullPost.builder().content("내용").build()));
				MainPostDto mainPostDto = MainPostDto.builder().build();

				//when
				//then
				assertThatThrownBy(() ->
					simplePostRepository.findRecruitingPostBySearchCondition("InvalidParam", mainPostDto))
					.isInstanceOf(InvalidParamException.class)
					.hasMessage(ErrorMessage.INVALID_POST_CATEGORY.getMessage());
			}

			@Test
			@DisplayName("ALL 로 들어오면 카테고리와 상관없이 게시글들을 반환한다")
			void ALL() throws Exception {
				//given
				User user = userRepository.save(createUser());
				simplePostRepository.save(createSimplePostWithCategory(user, PostCategory.PROJECT));
				simplePostRepository.save(createSimplePostWithCategory(user, PostCategory.MOGAKKO));
				MainPostDto mainPostDto = MainPostDto.builder().build();

				//when
				List<SimplePost> findSimplePosts = simplePostRepository.findRecruitingPostBySearchCondition(
					"ALL", mainPostDto);

				//then
				assertThat(findSimplePosts.size()).isEqualTo(2);
			}

			@Test
			@DisplayName("PROJECT 로 들어오면 PROJECT 카테고리에 해당하는 게시글을 반환한다.")
			void PROJECT() throws Exception {
				//given
				User user = userRepository.save(createUser());
				simplePostRepository.save(createSimplePostWithCategory(user, PostCategory.PROJECT));
				simplePostRepository.save(createSimplePostWithCategory(user, PostCategory.MOGAKKO));
				MainPostDto mainPostDto = MainPostDto.builder().build();

				//when
				List<SimplePost> findSimplePosts = simplePostRepository.findRecruitingPostBySearchCondition(
					"PROJECT", mainPostDto);

				//then
				assertThat(findSimplePosts.get(0).getCategory()).isEqualTo(PostCategory.PROJECT);
				assertThat(findSimplePosts.size()).isEqualTo(1);
			}
		}

		@Nested
		@DisplayName("Region 인자가")
		class RegionParameterIs {

			@Test
			@DisplayName("null값이면 region과 관계없이 게시글들을 반환한다.")
			void nullThan() throws Exception {
				//given
				User user = userRepository.save(createUser());
				simplePostRepository.save(createSimplePostWithRegion(user, Region.BUSAN));
				simplePostRepository.save(createSimplePostWithRegion(user, Region.DAEGU));
				MainPostDto mainPostDto = MainPostDto.builder().region(null).build();

				//when
				List<SimplePost> findSimplePosts = simplePostRepository.findRecruitingPostBySearchCondition(
					"ALL", mainPostDto);

				//then
				assertThat(findSimplePosts.size()).isEqualTo(2);
			}

			@Test
			@DisplayName("BUSAN 값이면 부산지역에 해당하는 게시글들을 반환한다.")
			void busan() throws Exception {
				//given
				User user = userRepository.save(createUser());
				simplePostRepository.save(createSimplePostWithRegion(user, Region.BUSAN));
				simplePostRepository.save(createSimplePostWithRegion(user, Region.DAEGU));
				MainPostDto mainPostDto = MainPostDto.builder().region(Region.BUSAN).build();

				//when
				List<SimplePost> findSimplePosts = simplePostRepository.findRecruitingPostBySearchCondition(
					"ALL", mainPostDto);

				//then
				assertThat(findSimplePosts.size()).isEqualTo(1);
				assertThat(findSimplePosts.get(0).getRegion()).isEqualTo(Region.BUSAN);
			}
		}

		@Nested
		@DisplayName("searchCondition 인자가")
		class SearchConditionParameterIs {

			@Test
			@DisplayName("만약 null이나 값이 없다면 searchCondition과 상관없이 게시글들을 반환한다")
			void isBlankThan() throws Exception {
				//given
				User user = userRepository.save(createUser());
				simplePostRepository.save(createSimplePostWithTitle(user, "으어어"));
				simplePostRepository.save(createSimplePostWithTitle(user, "으아악"));
				MainPostDto mainPostDto = MainPostDto.builder().searchCondition("").build();

				//when
				List<SimplePost> findSimplePosts = simplePostRepository.findRecruitingPostBySearchCondition(
					"ALL", mainPostDto);

				//then
				assertThat(findSimplePosts.size()).isEqualTo(2);
			}

			@Test
			@DisplayName("만약 값이 있다면 게시글 제목에 해당 값을 포함되는 게시글들을 반환한다")
			void isNotBlankThan() throws Exception {
				//given
				User user = userRepository.save(createUser());
				simplePostRepository.save(createSimplePostWithTitle(user, "가나다"));
				simplePostRepository.save(createSimplePostWithTitle(user, "가나라"));
				simplePostRepository.save(createSimplePostWithTitle(user, "바사아"));
				MainPostDto mainPostDto = MainPostDto.builder().searchCondition("가나").build();
				//when
				List<SimplePost> findSimplePosts = simplePostRepository.findRecruitingPostBySearchCondition(
					"ALL", mainPostDto);

				//then
				assertThat(findSimplePosts.size()).isEqualTo(2);
				assertThat(findSimplePosts.stream()
					.allMatch(simplePost -> simplePost.getTitle().contains("가나"))).isTrue();
			}
		}
	}

	@Nested
	@DisplayName("save 메서드는")
	class SaveMethodIs {

		@Nested
		@DisplayName("SimplePost 엔티티를 저장하면")
		class SaveSimplePost {

			@Test
			@DisplayName("FullPost 엔티티까지 저장한다.")
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
			@DisplayName("Matching 엔티티까지 저장한다.")
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