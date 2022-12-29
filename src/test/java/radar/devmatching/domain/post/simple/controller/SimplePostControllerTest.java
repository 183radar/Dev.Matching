package radar.devmatching.domain.post.simple.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.stream.IntStream;

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
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;
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
			params.add("userNum", "3");
			params.add("content", "내용이다");

			MockHttpServletRequestBuilder request = post(BASIC_URL + "/new")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.params(params);
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
		@DisplayName("폼 파라미터에")
		class CreatePostRequestParam {

			@Test
			@DisplayName("값이 없으면 게시글을 만들지 않고 현재 페이지를 반환한다")
			void nullCheck() throws Exception {
				//given
				//when
				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("title", "");
				params.add("category", "");
				params.add("region", "");
				params.add("userNum", "");
				params.add("content", "");

				MockHttpServletRequestBuilder request = post(BASIC_URL + "/new")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);
				ResultActions result = mockMvc.perform(request);

				//then
				verify(simplePostService, never()).createPost(any(CreatePostRequest.class), any(User.class));

				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(SimplePostController.class))
					.andExpect(handler().methodName("createPost"))
					.andExpect(model().attributeErrorCount("createPostRequest", 5))
					.andExpect(view().name("post/createPost"))
					.andDo(print());
			}

			@Test
			@DisplayName("title 글자수가 200이 넘거나, content 글자수가 10000이 넘으면 게시글을 만들지 않고 현재 페이지를 반환한다")
			void lengthCheck() throws Exception {
				//given
				StringBuilder titleSB = new StringBuilder();
				StringBuilder contentSB = new StringBuilder();
				IntStream.rangeClosed(1, 201).forEach(i -> titleSB.append("제"));
				IntStream.rangeClosed(1, 10001).forEach(i -> contentSB.append("내"));

				//when
				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("title", titleSB.toString());
				params.add("category", "PROJECT");
				params.add("region", "SEOUL");
				params.add("userNum", "3");
				params.add("content", contentSB.toString());

				MockHttpServletRequestBuilder request = post(BASIC_URL + "/new")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);
				ResultActions result = mockMvc.perform(request);

				//then
				verify(simplePostService, never()).createPost(any(CreatePostRequest.class), any(User.class));

				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(SimplePostController.class))
					.andExpect(handler().methodName("createPost"))
					.andExpect(model().attributeErrorCount("createPostRequest", 2))
					.andExpect(model().attributeHasFieldErrors("createPostRequest", "title"))
					.andExpect(model().attributeHasFieldErrors("createPostRequest", "content"))
					.andExpect(view().name("post/createPost"))
					.andDo(print());
			}

			@Test
			@DisplayName("userNum 숫자가 2보다 작거나 100보다 크면 게시글을 만들지 않고 현재 페이지를 반환한다")
			void rangeCheck() throws Exception {
				//given
				String userNumMoreThan = "1";
				String maxUserLessThan = "101";

				//when
				MultiValueMap<String, String> params1 = new LinkedMultiValueMap<>();
				params1.add("title", "제목이다");
				params1.add("category", "PROJECT");
				params1.add("region", "SEOUL");
				params1.add("userNum", userNumMoreThan);
				params1.add("content", "내용이다");

				MockHttpServletRequestBuilder request1 = post(BASIC_URL + "/new")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params1);
				ResultActions result1 = mockMvc.perform(request1);

				MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
				params2.add("title", "제목이다");
				params2.add("category", "PROJECT");
				params2.add("region", "SEOUL");
				params2.add("userNum", maxUserLessThan);
				params2.add("content", "내용이다");

				MockHttpServletRequestBuilder request2 = post(BASIC_URL + "/new")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params2);
				ResultActions result2 = mockMvc.perform(request2);

				//then
				verify(simplePostService, never()).createPost(any(CreatePostRequest.class), any(User.class));

				result1.andExpect(status().isOk())
					.andExpect(handler().handlerType(SimplePostController.class))
					.andExpect(handler().methodName("createPost"))
					.andExpect(model().attributeErrorCount("createPostRequest", 1))
					.andExpect(model().attributeHasFieldErrors("createPostRequest", "userNum"))
					.andExpect(view().name("post/createPost"))
					.andDo(print());
				result2.andExpect(status().isOk())
					.andExpect(handler().handlerType(SimplePostController.class))
					.andExpect(handler().methodName("createPost"))
					.andExpect(model().attributeErrorCount("createPostRequest", 1))
					.andExpect(model().attributeHasFieldErrors("createPostRequest", "userNum"))
					.andExpect(view().name("post/createPost"))
					.andDo(print());
			}
		}
	}

	@Nested
	@DisplayName("getMyPosts 메서드는")
	class GetMyPostsMethod {

		@Test
		@DisplayName("정상 흐름일 경우 내 게시글들을 모델에 담아 반환한다.")
		void correct() throws Exception {
			//given
			given(simplePostService.getMyPosts(anyLong())).willReturn(new ArrayList<SimplePostResponse>());

			//when
			ResultActions result = mockMvc.perform(get(BASIC_URL + "/my"));

			//then
			verify(simplePostService).getMyPosts(anyLong());

			result.andExpect(status().isOk())
				.andExpect(handler().handlerType(SimplePostController.class))
				.andExpect(handler().methodName("getMyPosts"))
				.andExpect(model().hasNoErrors())
				.andExpect(model().attributeExists("myPosts"))
				.andExpect(view().name("post/myPosts"));
		}
	}

	@Nested
	@DisplayName("getApplicationPosts 메서드는")
	class GetApplicationPostsMethod {

		@Test
		@DisplayName("정상 흐름일 경우 내가 신청한 게시글들을 모델에 담아 반환한다.")
		void correct() throws Exception {
			//given
			given(simplePostService.getApplicationPosts(anyLong())).willReturn(new ArrayList<SimplePostResponse>());

			//when
			ResultActions result = mockMvc.perform(get(BASIC_URL + "/applicationPosts"));

			//then
			verify(simplePostService).getApplicationPosts(anyLong());

			result.andExpect(status().isOk())
				.andExpect(handler().handlerType(SimplePostController.class))
				.andExpect(handler().methodName("getApplicationPosts"))
				.andExpect(model().hasNoErrors())
				.andExpect(model().attributeExists("applicationPosts"))
				.andExpect(view().name("post/applicationPosts"));
		}
	}
}