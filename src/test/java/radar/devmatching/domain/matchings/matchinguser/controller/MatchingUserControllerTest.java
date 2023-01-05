package radar.devmatching.domain.matchings.matchinguser.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUserRole;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserService;
import radar.devmatching.domain.matchings.matchinguser.service.dto.response.MatchingUserResponse;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.util.ControllerTestSetUp;

@Slf4j
@WebMvcTest(MatchingUserController.class)
@DisplayName("MatchingUserController의")
public class MatchingUserControllerTest extends ControllerTestSetUp {

	private static final String BASIC_URL = "/api/matching";
	private static final String MATCHING_USER_LIST = "matchingUserList";
	private static final Long USER_ID = 1L;
	private static final Long MATCHING_ID = 2L;

	@MockBean
	MatchingUserService matchingUserService;

	private MatchingUser getMatchingUser() {
		User user = createUser(USER_ID);
		Matching matching = createMatching();
		MatchingUser matchingUser = MatchingUser.builder()
			.user(user)
			.matching(matching)
			.matchingUserRole(MatchingUserRole.USER)
			.build();
		return matchingUser;
	}

	private Matching createMatching() {

		Matching matching = Matching.builder()
			.build();

		ReflectionTestUtils.setField(matching, "id", MATCHING_ID);

		SimplePost simplePost = SimplePost.builder()
			.leader(createUser(USER_ID))
			.fullPost(FullPost.builder().content("test").build())
			.title("test")
			.matching(matching)
			.build();

		return matching;
	}

	@Test
	@DisplayName("getMatchingUserList 메서드는 정상흐름에서 모델에 MatchingUserResponse 리스트를 넣어준다.")
	void getMatchingUserList() throws Exception {
		//given
		List<MatchingUserResponse> list = new ArrayList<>();
		list.add(MatchingUserResponse.of(getMatchingUser()));
		given(matchingUserService.getMatchingUserList(USER_ID)).willReturn(list);
		MockHttpServletRequestBuilder request = get(BASIC_URL + "/list");
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isOk())
			.andExpect(handler().handlerType(MatchingUserController.class))
			.andExpect(handler().methodName("getMatchingUserList"))
			.andExpect(model().attributeExists(MATCHING_USER_LIST))
			.andDo(print());
	}

}
