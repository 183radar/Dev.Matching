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
import radar.devmatching.util.ControllerTestSetUp;

@WebMvcTest(SubCommentController.class)
public class SubCommentControllerTest extends ControllerTestSetUp {

	private static final String BASIC_URL = "/api";

	@MockBean
	CommentService commentService;

	@Nested
	@DisplayName("getCreateSubComment 메서드는")
	class GetCreateSubCommentMethod {

		@Nested
		@DisplayName("mainCommentId 파라미터가")
		class MainCommentIdParam {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 createSubComment를 모델에 넘기고 뷰를 반환한다")
			void correct() throws Exception {
				//given
				Long mainCommentId = 1L;

				//when
				ResultActions result = mockMvc.perform(
					get(BASIC_URL + "/mainComments/" + mainCommentId + "/createSubComment"));

				//then
				verify(commentService).mainCommentExistById(eq(mainCommentId));

				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(SubCommentController.class))
					.andExpect(handler().methodName("getCreateSubComment"))
					.andExpect(model().attribute("createCommentRequest", hasProperty("entityId")))
					.andExpect(model().attribute("createCommentRequest",
						hasProperty("commentType", equalTo(CreateCommentRequest.CommentType.SUB))))
					.andExpect(view().name("comment/createComment"));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				willThrow(new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND)).given(commentService)
					.mainCommentExistById(anyLong());

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/mainComments/1/createSubComment"));

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(model().attributeDoesNotExist("getCreateSubComment"))
					.andExpect(view().name("error/404"));
			}
		}
	}

	@Nested
	@DisplayName("createSubComment 메서드는")
	class CreateSubCommentMethod {

		@Nested
		@DisplayName("mainCommentId 파라미터가")
		class MainCommentIdParam {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 SubComment 생성 후 redirect 한다")
			void correct() throws Exception {
				//given
				Long mainCommentId = 1L;
				Long simplePostId = 2L;
				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("content", "내용이다");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/mainComments/" + mainCommentId + "/createSubComment")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				given(commentService.createSubComment(eq(mainCommentId), anyLong(), any(CreateCommentRequest.class)))
					.willReturn(simplePostId);

				//when
				ResultActions result = mockMvc.perform(request);

				//then
				result.andExpect(status().isFound())
					.andExpect(handler().handlerType(SubCommentController.class))
					.andExpect(handler().methodName("createSubComment"))
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
					BASIC_URL + "/mainComments/1/createSubComment")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				willThrow(new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND)).given(commentService)
					.createSubComment(anyLong(), anyLong(), any());

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
				params.add("commentType", "SUB");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/mainComments/" + mainCommentId + "/createSubComment")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				//when
				ResultActions result = mockMvc.perform(request);

				//then
				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(SubCommentController.class))
					.andExpect(handler().methodName("createSubComment"))
					.andExpect(model().attributeHasFieldErrors("createCommentRequest", "content"))
					.andExpect(model().attribute("createCommentRequest", hasProperty("entityId")))
					.andExpect(model().attribute("createCommentRequest",
						hasProperty("commentType", equalTo(CreateCommentRequest.CommentType.SUB))))
					.andExpect(view().name("comment/createComment"));
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
				params.add("commentType", "SUB");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/mainComments/" + mainCommentId + "/createSubComment")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				//when
				ResultActions result = mockMvc.perform(request);

				//then
				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(SubCommentController.class))
					.andExpect(handler().methodName("createSubComment"))
					.andExpect(model().attributeHasFieldErrors("createCommentRequest", "content"))
					.andExpect(model().attribute("createCommentRequest", hasProperty("entityId")))
					.andExpect(model().attribute("createCommentRequest",
						hasProperty("commentType", equalTo(CreateCommentRequest.CommentType.SUB))))
					.andExpect(view().name("comment/createComment"));
			}
		}
	}

	@Nested
	@DisplayName("getUpdateSubComment 메서드는")
	class GetUpdateSubCommentMethod {

		@Nested
		@DisplayName("subCommentId 파라미터가")
		class subCommentIdParamIs {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 업데이트할 게시글을 가져와 updateCommentDto에 넣은 후 뷰를 반환한다")
			void correct() throws Exception {
				//given
				Long subCommentId = 1L;
				UpdateCommentDto updateCommentDto = UpdateCommentDto.builder()
					.entityId(subCommentId)
					.content("내용")
					.commentType(UpdateCommentDto.CommentType.SUB)
					.build();

				given(commentService.getSubCommentOnly(eq(subCommentId))).willReturn(updateCommentDto);

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/subComments/" + subCommentId + "/edit"));

				//then
				result.andExpect(status().isOk())
					.andExpect(handler().handlerType(SubCommentController.class))
					.andExpect(handler().methodName("getUpdateSubComment"))
					.andExpect(model().attribute("updateCommentDto", updateCommentDto));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				willThrow(new EntityNotFoundException(ErrorMessage.SUB_COMMENT_NOT_FOUND)).given(commentService)
					.getSubCommentOnly(anyLong());

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/subComments/1/edit"));

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(view().name("error/404"));
			}
		}
	}

	@Nested
	@DisplayName("updateSubComment 메서드는")
	class UpdateSubCommentMethod {

		@Nested
		@DisplayName("subCommentId 파라미터가")
		class SubCommentIdParamIs {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 댓글을 업데이트 하고 redirect 한다")
			void correct() throws Exception {
				//given
				Long subCommentId = 1L;
				Long simplePostId = 2L;
				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("content", "내용이다");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/subComments/" + subCommentId + "/edit")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				given(commentService.updateSubComment(eq(subCommentId), any(UpdateCommentDto.class), anyLong()))
					.willReturn(simplePostId);

				//when
				ResultActions result = mockMvc.perform(request);

				//then
				result.andExpect(status().isFound())
					.andExpect(handler().handlerType(SubCommentController.class))
					.andExpect(handler().methodName("updateSubComment"))
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
					BASIC_URL + "/subComments/1/edit")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				willThrow(new EntityNotFoundException(ErrorMessage.SUB_COMMENT_NOT_FOUND)).given(commentService)
					.updateSubComment(anyLong(), any(), anyLong());

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
				Long subCommentId = 1L;

				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("entityId", subCommentId.toString());
				params.add("content", "");
				params.add("commentType", "SUB");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/subComments/" + subCommentId + "/edit")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				//when
				ResultActions result1 = mockMvc.perform(request);

				//then
				result1.andExpect(status().isOk())
					.andExpect(handler().handlerType(SubCommentController.class))
					.andExpect(handler().methodName("updateSubComment"))
					.andExpect(model().attributeHasFieldErrors("updateCommentDto", "content"))
					.andExpect(model().attribute("updateCommentDto", hasProperty("entityId")))
					.andExpect(model().attribute("updateCommentDto",
						hasProperty("commentType", equalTo(UpdateCommentDto.CommentType.SUB))))
					.andExpect(view().name("comment/updateComment"));
			}

			@Test
			@DisplayName("content가 10000자가 넘으면 필드 에러 발생 후 같은 페이지로 돌아간다")
			void valid2() throws Exception {
				//given
				Long subCommentId = 1L;
				StringBuilder sb = new StringBuilder();
				IntStream.rangeClosed(1, 10001).forEach(i -> sb.append("a"));

				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("entityId", subCommentId.toString());
				params.add("content", sb.toString());
				params.add("commentType", "SUB");

				MockHttpServletRequestBuilder request = post(
					BASIC_URL + "/subComments/" + subCommentId + "/edit")
					.with(SecurityMockMvcRequestPostProcessors.csrf())
					.params(params);

				//when
				ResultActions result1 = mockMvc.perform(request);

				//then
				result1.andExpect(status().isOk())
					.andExpect(handler().handlerType(SubCommentController.class))
					.andExpect(handler().methodName("updateSubComment"))
					.andExpect(model().attributeHasFieldErrors("updateCommentDto", "content"))
					.andExpect(model().attribute("updateCommentDto", hasProperty("entityId")))
					.andExpect(model().attribute("updateCommentDto",
						hasProperty("commentType", equalTo(UpdateCommentDto.CommentType.SUB))))
					.andExpect(view().name("comment/updateComment"));
			}
		}
	}

	@Nested
	@DisplayName("deleteSubComment 메서드는")
	class DeleteSubCommentMethod {

		@Nested
		@DisplayName("subCommentId 파라미터가")
		class SubCommentIdParam {

			@Test
			@DisplayName("해당되는 엔티티가 있을 경우 댓글을 삭제하고 redirect 한다")
			void correct() throws Exception {
				//given
				Long simplePostId = 1L;
				Long subCommentId = 2L;
				given(commentService.deleteSubComment(eq(subCommentId), anyLong()))
					.willReturn(simplePostId);

				//when
				ResultActions result = mockMvc.perform(
					get(BASIC_URL + "/subComments/" + subCommentId + "/delete"));

				//then
				result.andExpect(status().isFound())
					.andExpect(handler().handlerType(SubCommentController.class))
					.andExpect(handler().methodName("deleteSubComment"))
					.andExpect(redirectedUrl("/api/posts/" + simplePostId));
			}

			@Test
			@DisplayName("해당되는 엔티티가 없을 경우 404가 전달된다")
			void notExistByCorrespondedEntity() throws Exception {
				//given
				willThrow(new EntityNotFoundException(ErrorMessage.SUB_COMMENT_NOT_FOUND)).given(commentService)
					.deleteSubComment(anyLong(), anyLong());

				//when
				ResultActions result = mockMvc.perform(get(BASIC_URL + "/subComments/1/delete"));

				//then
				result.andExpect(status().isNotFound())
					.andExpect(model().attribute("status", 404))
					.andExpect(view().name("error/404"));
			}
		}
	}
}