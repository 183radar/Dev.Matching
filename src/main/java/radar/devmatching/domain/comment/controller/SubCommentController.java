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
import radar.devmatching.common.security.jwt.JwtTokenInfo;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.UpdateCommentDto;
import radar.devmatching.domain.comment.service.dto.request.CreateCommentRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/")
public class SubCommentController {

	private final CommentService commentService;

	@GetMapping("mainComments/{mainCommentId}/createSubComment")
	public String getCreateSubComment(@PathVariable long mainCommentId, Model model) {
		commentService.mainCommentExistById(mainCommentId);
		model.addAttribute("createCommentRequest",
			CreateCommentRequest.of(mainCommentId, CreateCommentRequest.CommentType.SUB));
		return "comment/createComment";
	}

	@PostMapping("mainComments/{mainCommentId}/createSubComment")
	public String createSubComment(@AuthUser JwtTokenInfo jwtTokenInfo, @PathVariable long mainCommentId,
		@Valid @ModelAttribute CreateCommentRequest createCommentRequest, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			createCommentRequest.setEntityId(mainCommentId);
			createCommentRequest.setCommentType(CreateCommentRequest.CommentType.SUB);
			return "comment/createComment";
		}
		long simplePostId = commentService.createSubComment(mainCommentId, jwtTokenInfo.getUserId(),
			createCommentRequest);
		return "redirect:/api/posts/" + simplePostId;
	}

	@GetMapping("subComments/{subCommentId}/edit")
	public String getUpdateSubComment(@PathVariable long subCommentId, Model model) {
		UpdateCommentDto updateCommentDto = commentService.getSubCommentOnly(subCommentId);
		model.addAttribute("updateCommentDto", updateCommentDto);
		return "comment/updateComment";
	}

	@PostMapping("subComments/{subCommentId}/edit")
	public String updateSubComment(@AuthUser JwtTokenInfo jwtTokenInfo, @PathVariable long subCommentId,
		@Valid @ModelAttribute UpdateCommentDto updateCommentDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			updateCommentDto.setEntityId(subCommentId);
			updateCommentDto.setCommentType(UpdateCommentDto.CommentType.SUB);
			return "comment/updateComment";
		}
		long simplePostId = commentService.updateSubComment(subCommentId, updateCommentDto, jwtTokenInfo.getUserId());
		return "redirect:/api/posts/" + simplePostId;
	}

	@GetMapping("subComments/{subCommentId}/delete")
	public String deleteSubComment(@AuthUser JwtTokenInfo jwtTokenInfo, @PathVariable long subCommentId) {
		Long simplePostId = commentService.deleteSubComment(subCommentId, jwtTokenInfo.getUserId());
		return "redirect:/api/posts/" + simplePostId;
	}

}
