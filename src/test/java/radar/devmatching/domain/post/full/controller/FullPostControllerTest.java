package radar.devmatching.domain.post.full.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

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

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.full.service.FullPostService;
import radar.devmatching.domain.post.full.service.dto.UpdatePostDto;
import radar.devmatching.domain.post.full.service.dto.response.PresentPostResponse;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.exception.SimplePostNotFoundException;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.util.ControllerTestSetUp;

@WebMvcTest(FullPostController.class)
class FullPostControllerTest extends ControllerTestSetUp {

	private static final String BASIC_URL = "/api/posts";

	@MockBean
	FullPostService fullPostService;

	@Nested
	@DisplayName("getFullPost 메서드는")
	class GetFullPostMethod {

		@Nested
		@DisplayName("simplePostId 파라미터가")
		class SimplePostIdParam {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 presentPostResponse를 모델에 담고 view를 반환한다")
			void correct() throws Exception {
				//given
				Long simplePostId = 1L;
				User loginUser = createUser(2L);
				SimplePost simplePost = createSimplePost(loginUser, Matching.builder().build(),
					FullPost.builder().build(), simplePostId);
				PresentPostResponse presentPostResponse = PresentPostResponse.of(simplePost, loginUser,
					2, true, new ArrayList<>());
				given(fullPostService.getPostWithComment(anyLong(), anyLong()))
					.willReturn(presentPostResponse);

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/" + simplePostId));

				//then
				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(FullPostController.class))
					.andExpect(handler().methodName("getFullPost"))
					.andExpect(model().attribute("presentPostResponse", presentPostResponse))
					.andExpect(view().name("post/post"));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				given(fullPostService.getPostWithComment(anyLong(), anyLong()))
					.willThrow(new SimplePostNotFoundException());

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/1"));

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(view().name("error/404"));
			}
		}
	}

	@Nested
	@DisplayName("getUpdatePost 메서드는")
	class GetUpdatePostMethod {

		@Nested
		@DisplayName("simplePostId 파라미터가")
		class SimplePostIdParam {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 updatePostDto를 모델에 담고 view를 반환한다")
			void correct() throws Exception {
				//given
				Long simplePostId = 1L;
				SimplePost simplePost = createSimplePost(createUser(2L), Matching.builder().build(),
					FullPost.builder().build(), simplePostId);
				given(fullPostService.getUpdateFullPost(anyLong(), anyLong()))
					.willReturn(UpdatePostDto.of(simplePost));

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/" + simplePostId + "/edit"));

				//then
				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(FullPostController.class))
					.andExpect(handler().methodName("getUpdatePost"))
					.andExpect(model().attributeExists("updatePostDto"))
					.andExpect(view().name("post/editPost"));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				given(fullPostService.getUpdateFullPost(anyLong(), anyLong()))
					.willThrow(new SimplePostNotFoundException());

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/1/edit"));

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(view().name("error/404"));
			}
		}
	}

	@Nested
	@DisplayName("updatePost 메서드는")
	class UpdatePostMethod {

		@Nested
		@DisplayName("simplePostId 파라미터가")
		class SimplePostIdParam {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 게시글을 업데이트하고, redirect 한다")
			void correct() throws Exception {
				//given
				Long simplePostId = 1L;
				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("simplePostId", simplePostId.toString());
				params.add("title", "제목이다");
				params.add("category", "PROJECT");
				params.add("region", "BUSAN");
				params.add("userNum", "3");
				params.add("content", "내용이다");

				MockHttpServletRequestBuilder request = post(BASIC_URL + "/1/edit")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				//when
				ResultActions result = mockMvc.perform(request);

				//then
				result.andExpect(status().isFound())
					.andExpect(handler().handlerType(FullPostController.class))
					.andExpect(handler().methodName("updatePost"))
					.andExpect(model().hasNoErrors())
					.andExpect(redirectedUrl("/api/posts/" + simplePostId));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				Long simplePostId = 1L;
				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("simplePostId", simplePostId.toString());
				params.add("title", "제목이다");
				params.add("category", "PROJECT");
				params.add("region", "BUSAN");
				params.add("userNum", "3");
				params.add("content", "내용이다");

				MockHttpServletRequestBuilder request = post(BASIC_URL + "/1/edit")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				willThrow(new SimplePostNotFoundException()).given(fullPostService)
					.updatePost(anyLong(), anyLong(), any(UpdatePostDto.class));

				//when
				ResultActions result = mockMvc.perform(request);

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(view().name("error/404"));
			}
		}

		// @valid 검증해야 되는데 너무 반복적이라 생략하겠읍니다..
	}

	@Nested
	@DisplayName("deletePost 메서드는")
	class DeletePostMethod {

		@Nested
		@DisplayName("simplePostId 파라미터가")
		class SimplePostIdParam {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 게시글을 삭제하고 redirect 한다")
			void correct() throws Exception {
				//given
				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/1/delete"));

				//then
				result.andExpect(status().isFound())
					.andExpect(handler().handlerType(FullPostController.class))
					.andExpect(handler().methodName("deletePost"))
					.andExpect(redirectedUrl("/api/posts/my"));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				willThrow(new SimplePostNotFoundException()).given(fullPostService)
					.deletePost(anyLong(), anyLong());

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/1/delete"));

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(view().name("error/404"));
			}
		}
	}

	@Nested
	@DisplayName("closePost 메서드는")
	class ClosePostMethod {

		@Nested
		@DisplayName("simplePostId 파라미터가")
		class SimplePostIdParam {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 게시글을 삭제하고 redirect 한다")
			void correct() throws Exception {
				//given
				Long simplePostId = 1L;

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/" + simplePostId + "/end"));

				//then
				result.andExpect(status().isFound())
					.andExpect(handler().handlerType(FullPostController.class))
					.andExpect(handler().methodName("closePost"))
					.andExpect(redirectedUrl("/api/posts/" + simplePostId));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				willThrow(new SimplePostNotFoundException()).given(fullPostService)
					.closePost(anyLong(), anyLong());

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/1/end"));

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(view().name("error/404"));
			}
		}
	}
}