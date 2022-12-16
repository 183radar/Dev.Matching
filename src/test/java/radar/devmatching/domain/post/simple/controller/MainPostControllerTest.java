package radar.devmatching.domain.post.simple.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.post.simple.service.dto.MainPostDto;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.util.ControllerTestSetUp;
import radar.devmatching.util.WithCustomUser;

@WebMvcTest(MainPostController.class)
class MainPostControllerTest extends ControllerTestSetUp {

	private static final String categoryDefaultValue = "ALL";
	@MockBean
	SimplePostService simplePostService;

	@Nested
	@DisplayName("getMainPost 메서드는")
	@WithCustomUser
	class GetMainPostMethod {

		@Test
		@DisplayName("postCategory 파라미터가 안 넘어오면 전체를 조회한다")
		void noPostCategoryParamThanSelectAll() throws Exception {
			//given
			MainPostDto mainPostDto = MainPostDto.builder().build();
			given(simplePostService.getMainPostDto(any(User.class), eq(categoryDefaultValue)))
				.willReturn(mainPostDto);

			//when
			MockHttpServletRequestBuilder request = get("/");
			ResultActions result = mockMvc.perform(request);

			//then
			verify(simplePostService, only()).getMainPostDto(any(User.class), eq(categoryDefaultValue));

		}

	}
}