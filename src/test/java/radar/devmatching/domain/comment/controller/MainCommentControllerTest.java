package radar.devmatching.domain.comment.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.UpdateCommentDto;
import radar.devmatching.domain.comment.service.dto.request.CreateCommentRequest;
import radar.devmatching.domain.post.simple.exception.SimplePostNotFoundException;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.util.ControllerTestSetUp;

@WebMvcTest(MainCommentController.class)
public class MainCommentControllerTest extends ControllerTestSetUp {

	private static final String BASIC_URL = "/api";

	@MockBean
	SimplePostService simplePostService;
	@MockBean
	CommentService commentService;

	@Nested
	@DisplayName("getCreateMainComment 메서드는")
	class GetCreateMainCommentMethod {

		@Nested
		@DisplayName("simplePostId 파라미터가")
		class SimplePostIdParam {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 createMainComment를 모델에 넘기고 뷰를 반환한다")
			void correct() throws Exception {
				//given
				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/posts/1/createMainComment"));

				//then
				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(MainCommentController.class))
					.andExpect(handler().methodName("getCreateMainComment"))
					.andExpect(model().attribute("createCommentRequest", hasProperty("entityId")))
					.andExpect(model().attribute("createCommentRequest",
						hasProperty("commentType", equalTo(CreateCommentRequest.CommentType.MAIN))))
					.andExpect(view().name("comment/createComment"));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				willThrow(new SimplePostNotFoundException()).given(simplePostService)
					.findById(anyLong());

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/posts/1/createMainComment"));

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(model().attributeDoesNotExist("getCreateMainComment"))
					.andExpect(view().name("error/404"));
			}
		}
	}

	@Nested
	@DisplayName("createMainComment 메서드는")
	class CreateMainCommentMethod {

		@Nested
		@DisplayName("simplePostId 파라미터가")
		class SimplePostIdParam {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 MainComment 생성 후 redirect 한다")
			void correct() throws Exception {
				//given
				Long simplePostId = 1L;
				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("content", "내용이다");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/posts/" + simplePostId + "/createMainComment")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				//when
				ResultActions result = mockMvc.perform(request);

				//then
				result.andExpect(status().isFound())
					.andExpect(handler().handlerType(MainCommentController.class))
					.andExpect(handler().methodName("createMainComment"))
					.andExpect(model().hasNoErrors())
					.andExpect(redirectedUrl("/api/posts/" + simplePostId));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				Long simplePostId = 1L;
				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("content", "내용이다");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/posts/" + simplePostId + "/createMainComment")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				willThrow(new SimplePostNotFoundException()).given(commentService)
					.createMainComment(anyLong(), any(), any());

				//when
				ResultActions result = mockMvc.perform(request);

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(view().name("error/404"));
			}
		}

		@Nested
		@DisplayName("요청 파라미터에")
		class RequestParam {

			@Test
			@DisplayName("content가 빈 값이면 필드 에러 발생 후 같은 페이지로 돌아간다")
			void valid1() throws Exception {
				//given
				Long simplePostId = 1L;

				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("entityId", simplePostId.toString());
				params.add("content", "");
				params.add("commentType", "MAIN");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/posts/" + simplePostId + "/createMainComment")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				//when
				ResultActions result1 = mockMvc.perform(request);

				//then
				result1.andExpect(status().isOk())
					.andExpect(handler().handlerType(MainCommentController.class))
					.andExpect(handler().methodName("createMainComment"))
					.andExpect(model().attributeHasFieldErrors("createCommentRequest", "content"))
					.andExpect(model().attribute("createCommentRequest", hasProperty("entityId")))
					.andExpect(model().attribute("createCommentRequest",
						hasProperty("commentType", equalTo(CreateCommentRequest.CommentType.MAIN))))
					.andExpect(view().name("comment/createComment"));
			}

			@Test
			@DisplayName("content가 10000자가 넘으면 필드 에러 발생 후 같은 페이지로 돌아간다")
			void valid2() throws Exception {
				//given
				Long simplePostId = 1L;
				StringBuilder sb = new StringBuilder();
				IntStream.rangeClosed(1, 10001).forEach(i -> sb.append("a"));

				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("entityId", simplePostId.toString());
				params.add("content", sb.toString());
				params.add("commentType", "MAIN");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/posts/" + simplePostId + "/createMainComment")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				//when
				ResultActions result2 = mockMvc.perform(request);

				//then
				result2.andExpect(status().isOk())
					.andExpect(handler().handlerType(MainCommentController.class))
					.andExpect(handler().methodName("createMainComment"))
					.andExpect(model().attributeHasFieldErrors("createCommentRequest", "content"))
					.andExpect(model().attribute("createCommentRequest", hasProperty("entityId")))
					.andExpect(model().attribute("createCommentRequest",
						hasProperty("commentType", equalTo(CreateCommentRequest.CommentType.MAIN))))
					.andExpect(view().name("comment/createComment"));
			}
		}
	}

	@Nested
	@DisplayName("getUpdateMainComment 메서드는")
	class GetUpdateMainCommentMethod {

		@Nested
		@DisplayName("mainCommentId 파라미터가")
		class mainCommentIdParamIs {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 업데이트할 게시글을 가져와 updateCommentDto에 넣은 후 뷰를 반환한다")
			void correct() throws Exception {
				//given
				Long mainCommentId = 1L;
				UpdateCommentDto updateCommentDto = UpdateCommentDto.builder()
					.entityId(mainCommentId)
					.content("내용")
					.commentType(UpdateCommentDto.CommentType.MAIN)
					.build();

				given(commentService.getMainCommentOnly(eq(mainCommentId))).willReturn(updateCommentDto);

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/mainComments/" + mainCommentId + "/edit"));

				//then
				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(MainCommentController.class))
					.andExpect(handler().methodName("getUpdateMainComment"))
					.andExpect(model().attribute("updateCommentDto", updateCommentDto));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				willThrow(new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND)).given(commentService)
					.getMainCommentOnly(anyLong());

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/mainComments/1/edit"));

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(view().name("error/404"));
			}
		}
	}

	@Nested
	@DisplayName("updateMainComment 메서드는")
	class UpdateMainCommentMethod {

		@Nested
		@DisplayName("mainCommentId 파라미터가")
		class MainCommentIdParamIs {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 댓글을 업데이트 하고 redirect 한다")
			void correct() throws Exception {
				//given
				Long mainCommentId = 1L;
				Long simplePostId = 2L;
				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("content", "내용이다");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/mainComments/" + mainCommentId + "/edit")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				given(commentService.updateMainComment(eq(mainCommentId), any(UpdateCommentDto.class), any(User.class)))
					.willReturn(simplePostId);

				//when
				ResultActions result = mockMvc.perform(request);

				//then
				result.andExpect(status().isFound())
					.andExpect(handler().handlerType(MainCommentController.class))
					.andExpect(handler().methodName("updateMainComment"))
					.andExpect(model().hasNoErrors())
					.andExpect(redirectedUrl("/api/posts/" + simplePostId));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("content", "내용이다");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/mainComments/1/edit")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				willThrow(new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND)).given(commentService)
					.updateMainComment(anyLong(), any(), any());

				//when
				ResultActions result = mockMvc.perform(request);

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(view().name("error/404"));
			}
		}

		@Nested
		@DisplayName("요청 파라미터에")
		class RequestParam {

			@Test
			@DisplayName("content가 빈 값이면 필드 에러 발생 후 같은 페이지로 돌아간다")
			void valid1() throws Exception {
				//given
				Long mainCommentId = 1L;

				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("entityId", mainCommentId.toString());
				params.add("content", "");
				params.add("commentType", "MAIN");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/mainComments/" + mainCommentId + "/edit")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				//when
				ResultActions result1 = mockMvc.perform(request);

				//then
				result1.andExpect(status().isOk())
					.andExpect(handler().handlerType(MainCommentController.class))
					.andExpect(handler().methodName("updateMainComment"))
					.andExpect(model().attributeHasFieldErrors("updateCommentDto", "content"))
					.andExpect(model().attribute("updateCommentDto", hasProperty("entityId")))
					.andExpect(model().attribute("updateCommentDto",
						hasProperty("commentType", equalTo(UpdateCommentDto.CommentType.MAIN))))
					.andExpect(view().name("comment/updateComment"));
			}

			@Test
			@DisplayName("content가 10000자가 넘으면 필드 에러 발생 후 같은 페이지로 돌아간다")
			void valid2() throws Exception {
				//given
				Long mainCommentId = 1L;
				StringBuilder sb = new StringBuilder();
				IntStream.rangeClosed(1, 10001).forEach(i -> sb.append("a"));

				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("entityId", mainCommentId.toString());
				params.add("content", sb.toString());
				params.add("commentType", "MAIN");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/mainComments/" + mainCommentId + "/edit")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				//when
				ResultActions result1 = mockMvc.perform(request);

				//then
				result1.andExpect(status().isOk())
					.andExpect(handler().handlerType(MainCommentController.class))
					.andExpect(handler().methodName("updateMainComment"))
					.andExpect(model().attributeHasFieldErrors("updateCommentDto", "content"))
					.andExpect(model().attribute("updateCommentDto", hasProperty("entityId")))
					.andExpect(model().attribute("updateCommentDto",
						hasProperty("commentType", equalTo(UpdateCommentDto.CommentType.MAIN))))
					.andExpect(view().name("comment/updateComment"));
			}
		}
	}

	@Nested
	@DisplayName("deleteMainComment 메서드는")
	class DeleteMainCommentMethod {

		@Nested
		@DisplayName("mainCommentId 파라미터가")
		class MainCommentIdParam {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 댓글을 삭제하고 redirect 한다")
			void correct() throws Exception {
				//given
				Long simplePostId = 1L;
				Long mainCommentId = 2L;
				given(commentService.deleteMainComment(eq(mainCommentId), any(User.class)))
					.willReturn(simplePostId);

				//when
				ResultActions result = mockMvc.perform(
					get(BASIC_URL + "/mainComments/" + mainCommentId + "/delete"));

				//then
				result.andExpect(status().isFound())
					.andExpect(handler().handlerType(MainCommentController.class))
					.andExpect(handler().methodName("deleteMainComment"))
					.andExpect(redirectedUrl("/api/posts/" + simplePostId));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				willThrow(new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND)).given(commentService)
					.deleteMainComment(anyLong(), any());

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/mainComments/1/delete"));

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(view().name("error/404"));
			}
		}
	}
}