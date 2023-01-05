package radar.devmatching.util;

import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.UserService;

@Import({ControllerTestConfig.class})
@MockBean(JpaMetamodelMappingContext.class)
@WithCustomUser // 만약 UserRole 변경을 원하면 대상 메서드에 애노테이션을 붙여 설정 변경 가능
public abstract class ControllerTestSetUp {

	protected MockMvc mockMvc;

	@MockBean
	UserService userService;

	protected static User createUser(long userId) {
		User user = User.builder()
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("schoolName")
			.githubUrl("githubUrl")
			.introduce("introduce")
			.build();
		ReflectionTestUtils.setField(user, "id", userId);
		return user;
	}

	protected static SimplePost createSimplePost(User user, Matching matching, FullPost fullPost, long simplePostId) {
		SimplePost simplePost = SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.leader(user)
			.matching(matching)
			.fullPost(fullPost)
			.build();
		ReflectionTestUtils.setField(simplePost, "id", simplePostId);
		return simplePost;
	}

	@BeforeEach
	void setUp(WebApplicationContext context) {
		User customUser = User.builder().build();
		ReflectionTestUtils.setField(customUser, "id", 1L);
		BDDMockito.given(userService.getUserEntityByUsername(eq("CustomUserName")))
			.willReturn(customUser);

		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();
	}

}
