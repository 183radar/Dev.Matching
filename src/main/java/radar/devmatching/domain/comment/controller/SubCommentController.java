package radar.devmatching.domain.comment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.comment.service.CommentService;

@Controller
@RequiredArgsConstructor
public class SubCommentController {

	private final CommentService commentService;

	@PostMapping("mainComments/{mainCommentId}/createSubComment")
	public String createSubComment(@PathVariable String mainCommentId) {
		return "post/post";
	}

	@PostMapping("subComments/{subCommentId}/edit")
	public String updateSubComment(@PathVariable String subCommentId) {
		return "post/post";
	}

	@PostMapping("subComments/{subCommentId}/delete")
	public String deleteSubComment(@PathVariable String subCommentId) {
		return "post/post";
	}

}
