package radar.devmatching.domain.post.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.MainCommentDto;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.service.PostService;

@Controller
@RequiredArgsConstructor
@RequestMapping("posts")
public class FullPostController {

	private final PostService postService;
	private final CommentService commentService;

	@GetMapping("/{simplePostId}")
	public String getPost(@PathVariable long simplePostId, Model model) {
		SimplePost simplePost = postService.getPost(simplePostId);
		List<MainCommentDto> allComments = commentService.getAllComments(simplePost.getFullPost().getId());
		model.addAttribute("allComments", allComments);
		return "post/post";
	}

	@GetMapping("/{simplePostId}/edit")
	public String getUpdatePost(@PathVariable long simplePostId) {
		return "post/editPost";
	}

	@PostMapping("/{simplePostId}/edit")
	public String updatePost(@PathVariable long simplePostId) {
		return "post/editPost";
	}

	@PostMapping("/{simplePostId}/delete")
	public String deletePost(@PathVariable long simplePostId) {
		return "post/myPosts";
	}

	@GetMapping("/{simplePostId}/end")
	public String closePost(@PathVariable long simplePostId) {
		return "post/post";
	}

}
