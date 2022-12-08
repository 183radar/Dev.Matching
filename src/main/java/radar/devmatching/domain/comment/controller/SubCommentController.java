package radar.devmatching.domain.comment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.UpdateCommentDto;
import radar.devmatching.domain.comment.service.dto.request.CreateCommentRequest;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/")
public class SubCommentController {

	private final CommentService commentService;

	@GetMapping("mainComments/{mainCommentId}/createSubComment")
	public String getCreateSubComment(@PathVariable long mainCommentId, Model model) {
		commentService.mainCommentExistById(mainCommentId);
		model.addAttribute("createCommentRequest",
			CreateCommentRequest.of(CreateCommentRequest.CommentType.SUB));
		return "comment/createComment";
	}

	@PostMapping("mainComments/{mainCommentId}/createSubComment")
	public String createSubComment(@AuthUser User authUser, @PathVariable long mainCommentId,
		CreateCommentRequest createCommentRequest) {
		long simplePostId = commentService.createSubComment(mainCommentId, authUser, createCommentRequest);
		return "redirect:/api/posts/" + simplePostId;
	}

	@GetMapping("subComments/{subCommentId}/edit")
	public String getUpdateSubComment(@PathVariable long subCommentId, Model model) {
		UpdateCommentDto updateCommentDto = commentService.getSubCommentOnly(subCommentId);
		model.addAttribute("updateCommentDto", updateCommentDto);
		return "comment/updateComment";
	}

	@PostMapping("subComments/{subCommentId}/edit")
	public String updateSubComment(@AuthUser User authUser, @PathVariable long subCommentId,
		UpdateCommentDto updateCommentDto) {
		long simplePostId = commentService.updateSubComment(subCommentId, updateCommentDto, authUser);
		return "redirect:/api/posts/" + simplePostId;
	}

	@PostMapping("subComments/{subCommentId}/delete")
	public String deleteSubComment(@AuthUser User user, @PathVariable long subCommentId) {
		// commentService.deleteSubComment()

		return "redirect:/api/posts/my";
	}

}
