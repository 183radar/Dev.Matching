package radar.devmatching.domain.post.simple.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.post.simple.service.dto.MainPostDto;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.util.ControllerTestSetUp;

@WebMvcTest(MainPostController.class)
class MainPostControllerTest extends ControllerTestSetUp {

	private static final String categoryDefaultValue = "ALL";

	@MockBean
	SimplePostService simplePostService;

	@Nested
	@DisplayName("getMainPost 메서드는")
	class GetMainPageMethod {

		@Test
		@DisplayName("postCategory 파라미터에 따라 게시글들을 mainPostDto에 담아 model에 보내준다")
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
			result.andExpect(status().isOk())
				.andExpect(handler().handlerType(MainPostController.class))
				.andExpect(handler().methodName("getMainPage"))
				.andExpect(model().attributeExists("mainPostDto"));
		}
	}

	@Nested
	@DisplayName("searchMainPage 메서드는")
	class SearchMainPageMethod {

		@Nested
		@DisplayName("MainPostDto 파라미터에")
		class MainPostDtoParam {

			@Test
			@DisplayName("바인딩되는 searchCondition이 20글자가 넘어가면 에러를 낸다")
			void searchConditionLengthOver() throws Exception {
				//given
				MainPostDto mainPostDto = MainPostDto.builder().build();
				given(simplePostService.searchSimplePost(any(User.class), anyString(), any(MainPostDto.class)))
					.willReturn(mainPostDto);

				//when
				StringBuilder sb = new StringBuilder();
				IntStream.rangeClosed(1, 21).forEach(i -> sb.append("a"));
				MockHttpServletRequestBuilder request = post("/")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.param("region", "SEOUL")
					.param("searchCondition", sb.toString());
				ResultActions result = mockMvc.perform(request);

				//then
				verify(simplePostService, only()).searchSimplePost(any(User.class), anyString(),
					any(MainPostDto.class));

				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(MainPostController.class))
					.andExpect(handler().methodName("searchMainPage"))
					.andExpect(model().attributeHasFieldErrorCode(
						"mainPostDto", "searchCondition", "Length"))
					.andDo(print());
			}

			@Test
			@DisplayName("정상 흐름일 경우 에러 없이 model에 mainPostDto를 반환한다")
			void correct() throws Exception {
				//given
				MainPostDto mainPostDto = MainPostDto.builder().build();
				given(simplePostService.searchSimplePost(any(User.class), anyString(), any(MainPostDto.class)))
					.willReturn(mainPostDto);

				//when
				MockHttpServletRequestBuilder request = post("/")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.param("region", "SEOUL")
					.param("searchCondition", "condition");
				ResultActions result = mockMvc.perform(request);

				//then
				verify(simplePostService, only()).searchSimplePost(any(User.class), anyString(),
					any(MainPostDto.class));

				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(MainPostController.class))
					.andExpect(handler().methodName("searchMainPage"))
					.andExpect(model().hasNoErrors())
					.andDo(print());
			}
		}
	}
}