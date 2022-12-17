package radar.devmatching.domain.post.simple.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.post.simple.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.util.ControllerTestSetUp;

@WebMvcTest(SimplePostController.class)
class SimplePostControllerTest extends ControllerTestSetUp {

	private static final String BASIC_URL = "/api/posts";

	@MockBean
	SimplePostService simplePostService;

	@Test
	@DisplayName("getCreatePost 메서드는 빈 createPostRequest 객체를 모델에 넘긴다")
	void getCreatePostMethod() throws Exception {
		//given
		//when
		MockHttpServletRequestBuilder request = get(BASIC_URL + "/new");
		ResultActions result = mockMvc.perform(request);

		//then
		result.andExpect(status().isOk())
			.andExpect(handler().handlerType(SimplePostController.class))
			.andExpect(handler().methodName("getCreatePost"))
			.andExpect(model().attributeExists("createPostRequest"))
			.andDo(print());
	}

	@Nested
	@DisplayName("createPost 메서드는 ")
	class CreatePostMethod {

		@Test
		@DisplayName("정상 흐름이면 게시글 저장 후 게시글로 redirect 한다")
		void createPostMethod() throws Exception {
			//given
			Long simplePostId = 1L;
			given(simplePostService.createPost(any(CreatePostRequest.class), any(User.class)))
				.willReturn(simplePostId);

			//when
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("title", "제목이다");
			params.add("category", "PROJECT");
			params.add("region", "SEOUL");
			params.add("content", "내용이다");

			MockHttpServletRequestBuilder request = post(BASIC_URL + "/new")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("region", "SEOUL");
			// .params(params);
			ResultActions result = mockMvc.perform(request);

			//then
			verify(simplePostService, only()).createPost(any(CreatePostRequest.class), any(User.class));

			result.andExpect(status().isFound())
				.andExpect(handler().handlerType(SimplePostController.class))
				.andExpect(handler().methodName("createPost"))
				.andExpect(redirectedUrl(BASIC_URL + "/" + simplePostId))
				.andDo(print());
		}

		@Nested
		@DisplayName("CreatePostRequest 인자에")
		class CreatePostRequestParam {

			@Test
			@DisplayName("값이 없으면 게시글을 만들지 않고 ")
			void nullCheck() throws Exception {
				//given

				//when

				//then

			}
		}
	}
}