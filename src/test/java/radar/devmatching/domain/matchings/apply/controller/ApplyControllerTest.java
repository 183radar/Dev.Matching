package radar.devmatching.domain.matchings.apply.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import radar.devmatching.domain.matchings.apply.service.ApplyService;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyResponse;
import radar.devmatching.util.ControllerTestSetUp;

@WebMvcTest(ApplyController.class)
@DisplayName("ApplyController의")
public class ApplyControllerTest extends ControllerTestSetUp {

	private static final Long SIMPLE_POST_ID = 1L;

	@MockBean
	ApplyService applyService;

	@Test
	@DisplayName("createApply 메서드는 사용자가 해당 게시글을 신청하면 게시글로 리다리엑트한다.")
	void createApply() throws Exception {
		//given
		MockHttpServletRequestBuilder request = get("/api/posts/" + SIMPLE_POST_ID + "/apply");
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isFound())
			.andExpect(handler().handlerType(ApplyController.class))
			.andExpect(handler().methodName("createApply"))
			.andExpect(redirectedUrl("/api/posts/" + SIMPLE_POST_ID))
			.andDo(print());

		verify(applyService, times(1)).createApply(anyLong(), anyLong());
	}

	@Test
	@DisplayName("getApplyListInUser 메서드는 사용자가 신청한 현황 페이지로 이동한다.")
	void getApplyListInUser() throws Exception {
		//given
		given(applyService.getAllApplyList(anyLong())).willReturn(new ArrayList<ApplyResponse>());
		MockHttpServletRequestBuilder request = get("/api/users/apply");
		//when
		ResultActions result = mockMvc.perform(request);
		//then
		result.andExpect(status().isOk())
			.andExpect(handler().handlerType(ApplyController.class))
			.andExpect(handler().methodName("getApplyListInUser"))
			.andExpect(model().attributeExists("applyList"))
			.andDo(print());
	}

}
