package radar.devmatching.domain.comment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.CreateCommentDto;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
public class MainCommentController {

	private final CommentService commentService;

	@PostMapping("posts/{simplePostId}/createMainComment")
	public String createMainComment(@AuthUser User authUser, @PathVariable long simplePostId,
		@ModelAttribute CreateCommentDto createCommentDto) {
		// commentService.createMainComment(simplePostId, authUser, commentDto);
		return "post/post";
	}

	@PostMapping("mainComments/{mainCommentId}/edit")
	public String updateMainComment(@PathVariable long mainCommentId) {
		return "post/post";
	}

	@PostMapping("mainComments/{mainCommentId}/delete")
	public String deleteMainComment(@PathVariable long mainCommentId) {
		return "post/post";
	}

}
