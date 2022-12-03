package radar.devmatching.domain.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.post.service.PostService;
import radar.devmatching.domain.post.service.dto.request.UpdatePostRequest;
import radar.devmatching.domain.post.service.dto.response.PresentPostResponse;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class FullPostController {

	private final PostService postService;

	@GetMapping("/{simplePostId}")
	public String getAllPost(@PathVariable long simplePostId, Model model) {
		PresentPostResponse presentPostResponse = postService.getPostWithComment(simplePostId);
		model.addAttribute("PresentPostResponse", presentPostResponse);
		return "post/post";
	}

	@GetMapping("/{simplePostId}/edit")
	public String getUpdatePost(@AuthUser User authUser, @PathVariable long simplePostId, Model model) {
		UpdatePostRequest findPost = postService.getFullPost(simplePostId, authUser.getId());
		model.addAttribute("UpdatePostRequest", findPost);
		return "post/editPost";
	}

	@PostMapping("/{simplePostId}/edit")
	public String updatePost(@PathVariable long simplePostId, @ModelAttribute UpdatePostRequest updatePostRequest) {
		postService.updatePost(simplePostId, updatePostRequest);
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
