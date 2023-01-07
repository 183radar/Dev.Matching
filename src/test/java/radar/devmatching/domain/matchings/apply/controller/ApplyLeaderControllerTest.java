package radar.devmatching.domain.matchings.apply.controller;

import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import radar.devmatching.domain.matchings.apply.service.ApplyLeaderService;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyLeaderResponse;
import radar.devmatching.util.ControllerTestSetUp;

@WebMvcTest(ApplyLeaderController.class)
@DisplayName("ApplyLeaderController의")
public class ApplyLeaderControllerTest extends ControllerTestSetUp {
	static final String BASIC_URL = "/api/posts";
	static final Long USER_ID = 1L;
	static final Long SIMPLE_POST_ID = 2L;
	static final Long APPLY_ID = 3L;
	static final String BASIC_URL_APPLY_LIST = BASIC_URL + "/" + SIMPLE_POST_ID + "/apply/users";

	@MockBean
	ApplyLeaderService applyLeaderService;

	@Test
	@DisplayName("getApplyListInPost 메서드는 simplePostId에 해당하는 신청 현황 페이지로 이동한다.")
	void getApplyListInPost() throws Exception {
		//given
		List<ApplyLeaderResponse> list = new ArrayList<>();
		given(applyLeaderService.getAllApplyList(eq(USER_ID), eq(SIMPLE_POST_ID))).willReturn(list);
		MockHttpServletRequestBuilder request = get(BASIC_URL_APPLY_LIST);
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isOk())
			.andExpect(handler().handlerType(ApplyLeaderController.class))
			.andExpect(handler().methodName("getApplyListInPost"))
			.andExpect(model().attributeExists("applyList"))
			.andDo(print());

	}

	@Test
	@DisplayName("updateApplyAccept 메서드는 apply의 상태를 Accepted로 바꾼후 신청 현황 페이지로 리다이렉트한다.")
	void updateApplyAccept() throws Exception {
		//given
		MockHttpServletRequestBuilder request = get(BASIC_URL_APPLY_LIST + "/" + APPLY_ID + "/accept");
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isFound())
			.andExpect(redirectedUrl(BASIC_URL_APPLY_LIST))
			.andExpect(handler().handlerType(ApplyLeaderController.class))
			.andExpect(handler().methodName("updateApplyAccept"))
			.andDo(print());
	}

	@Test
	@DisplayName("updateApplyDeny 메서드는 apply의 상태를 deny로 바꾼후 신청 현황 페이지로 이동한다.")
	void updateApplyDeny() throws Exception {
		//given
		MockHttpServletRequestBuilder request = get(BASIC_URL_APPLY_LIST + "/" + APPLY_ID + "/deny");
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isFound())
			.andExpect(redirectedUrl(BASIC_URL_APPLY_LIST))
			.andExpect(handler().handlerType(ApplyLeaderController.class))
			.andExpect(handler().methodName("updateApplyDeny"))
			.andDo(print());
	}
}
