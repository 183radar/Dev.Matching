package radar.devmatching.domain.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.comment.service.dto.CommentDto;
import radar.devmatching.domain.post.service.PostService;

@Controller
@RequiredArgsConstructor
@RequestMapping("posts")
public class FullPostController {

	private final PostService postService;

	@GetMapping("/{simplePostId}")
	public String getPost(@PathVariable long simplePostId, Model model) {
		model.addAttribute("createCommentDto", CommentDto.of());
		return "post/post";
	}

	@PostMapping("/{simplePostId}/edit")
	public String updatePost(@PathVariable long simplePostId) {
		return "post/post";
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
