package radar.devmatching.domain.comment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import radar.devmatching.domain.post.service.PostService;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/")
public class MainCommentController {

	private final PostService postService;
	private final CommentService commentService;

	@GetMapping("posts/{simplePostId}/createMainComment")
	public String getCreateMainComment(@PathVariable long simplePostId, Model model) {
		// 해당 simplePost가 존재하는지 검증
		postService.getSimplePostOnly(simplePostId);
		model.addAttribute("createCommentRequest",
			CreateCommentRequest.of(CreateCommentRequest.CommentType.MAIN));
		return "comment/createComment";
	}

	@PostMapping("posts/{simplePostId}/createMainComment")
	public String createMainComment(@AuthUser User authUser, @PathVariable long simplePostId,
		@ModelAttribute CreateCommentRequest createCommentRequest) {
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
		UpdateCommentDto updateCommentDto) {
		long simplePostId = commentService.updateMainComment(mainCommentId, updateCommentDto, authUser);
		return "redirect:/api/posts/" + simplePostId;
	}

	@GetMapping("mainComments/{mainCommentId}/delete")
	public String deleteMainComment(@AuthUser User authUser, @PathVariable long mainCommentId) {
		commentService.deleteMainComment(mainCommentId, authUser);
		return "redirect:/api/posts/my";
	}

}
