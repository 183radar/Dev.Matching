package radar.devmatching.domain.matchings.matching.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import radar.devmatching.domain.matchings.matching.service.MatchingService;
import radar.devmatching.domain.matchings.matching.service.dto.MatchingInfo;
import radar.devmatching.domain.matchings.matching.service.dto.MatchingUpdate;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUserRole;
import radar.devmatching.domain.post.simple.entity.PostState;
import radar.devmatching.util.ControllerTestSetUp;

@WebMvcTest(MatchingController.class)
@DisplayName("MatchingController의")
class MatchingControllerTest extends ControllerTestSetUp {

	private static final String BASIC_URL = "/api/matching";
	private static final Long USER_ID = 1L;
	private static final Long MATCHING_ID = 2L;
	private static final String BASIC_URL_MATCHING_ID = BASIC_URL + "/" + MATCHING_ID;

	@MockBean
	MatchingService matchingService;

	@Test
	@DisplayName("getMatchingPage 메서드는 matching 정보를 모델에 담아 보여준다.")
	void getMatchingPage() throws Exception {
		//given
		MatchingInfo matchingInfo = MatchingInfo.builder()
			.matchingTitle("test")
			.matchingInfo("test")
			.matchingUserRole(MatchingUserRole.LEADER)
			.matchingUserList(new ArrayList<>())
			.userCount(0)
			.postState(PostState.RECRUITING)
			.build();
		given(matchingService.getMatchingInfo(any(), anyLong())).willReturn(matchingInfo);
		MockHttpServletRequestBuilder request = get(BASIC_URL_MATCHING_ID);
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isOk())
			.andExpect(handler().handlerType(MatchingController.class))
			.andExpect(handler().methodName("getMatchingPage"))
			.andExpect(model().attributeExists("matching"))
			.andDo(print());
	}

	@Test
	@DisplayName("updateMatchingPage 메서드는 Matching 정보 변경 페이지로 이동한다.")
	void updateMatchingPage() throws Exception {
		//given
		MatchingUpdate matchingUpdate = MatchingUpdate.builder()
			.matchingId(MATCHING_ID)
			.matchingTitle("test")
			.matchingInfo("test")
			.build();
		given(matchingService.getMatchingUpdateData(MATCHING_ID)).willReturn(matchingUpdate);
		MockHttpServletRequestBuilder request = get(BASIC_URL_MATCHING_ID + "/update");
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isOk())
			.andExpect(handler().handlerType(MatchingController.class))
			.andExpect(handler().methodName("updateMatchingPage"))
			.andExpect(model().attributeExists("matchingUpdate"))
			.andDo(print());
	}

	@Test
	@DisplayName("updateMatching 메서드는 matchingUpdate 객체 정보를 통해 matching을 변경하고 matching정보 페이지로 이동한다.")
	void updateMatching() throws Exception {
		//given

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("matchingId", "1");
		params.add("matchingTitle", "test");
		params.add("matchingInfo", "test");

		MockHttpServletRequestBuilder request = post(BASIC_URL_MATCHING_ID + "/update")
			.params(params)
			.with(SecurityMockMvcRequestPostProcessors.csrf());
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isFound())
			.andExpect(redirectedUrl(BASIC_URL_MATCHING_ID))
			.andExpect(handler().handlerType(MatchingController.class))
			.andExpect(handler().methodName("updateMatching"))
			.andDo(print());
	}
}