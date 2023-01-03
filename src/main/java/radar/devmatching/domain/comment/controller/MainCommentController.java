package radar.devmatching.domain.comment.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.UpdateCommentDto;
import radar.devmatching.domain.comment.service.dto.request.CreateCommentRequest;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/")
public class MainCommentController {

	private final SimplePostService simplePostService;
	private final CommentService commentService;

	@GetMapping("posts/{simplePostId}/createMainComment")
	public String getCreateMainComment(@PathVariable long simplePostId, Model model) {
		simplePostService.findById(simplePostId);
		model.addAttribute("createCommentRequest",
			CreateCommentRequest.of(simplePostId, CreateCommentRequest.CommentType.MAIN));
		return "comment/createComment";
	}

	@PostMapping("posts/{simplePostId}/createMainComment")
	public String createMainComment(@AuthUser User authUser, @PathVariable long simplePostId,
		@Valid @ModelAttribute CreateCommentRequest createCommentRequest, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			createCommentRequest.setEntityId(simplePostId);
			createCommentRequest.setCommentType(CreateCommentRequest.CommentType.MAIN);
			return "comment/createComment";
		}
		commentService.createMainComment(simplePostId, authUser, createCommentRequest);
		return "redirect:/api/posts/" + simplePostId;
	}

	@GetMapping("mainComments/{mainCommentId}/edit")
	public String getUpdateMainComment(@PathVariable long mainCommentId, Model model) {
		UpdateCommentDto updateCommentDto = commentService.getMainCommentOnly(mainCommentId);
		model.addAttribute("updateCommentDto", updateCommentDto);
		return "comment/updateComment";
	}

	@PostMapping("mainComments/{mainCommentId}/edit")
	public String updateMainComment(@AuthUser User authUser, @PathVariable long mainCommentId,
		@Valid @ModelAttribute UpdateCommentDto updateCommentDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			updateCommentDto.setEntityId(mainCommentId);
			updateCommentDto.setCommentType(UpdateCommentDto.CommentType.MAIN);
			return "comment/updateComment";
		}
		long simplePostId = commentService.updateMainComment(mainCommentId, updateCommentDto, authUser);
		return "redirect:/api/posts/" + simplePostId;
	}

	@GetMapping("mainComments/{mainCommentId}/delete")
	public String deleteMainComment(@AuthUser User authUser, @PathVariable long mainCommentId) {
		Long simplePostId = commentService.deleteMainComment(mainCommentId, authUser);
		return "redirect:/api/posts/" + simplePostId;
	}

}
