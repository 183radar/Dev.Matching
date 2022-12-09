package radar.devmatching.domain.post.full.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.post.full.service.FullPostService;
import radar.devmatching.domain.post.simple.service.dto.UpdatePostDto;
import radar.devmatching.domain.post.simple.service.dto.response.PresentPostResponse;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/posts")
public class FullPostController {

	private final FullPostService fullPostService;

	@GetMapping("/{simplePostId}")
	public String getFullPost(@PathVariable long simplePostId, Model model) {
		PresentPostResponse presentPostResponse = fullPostService.getPostWithComment(simplePostId);
		model.addAttribute("PresentPostResponse", presentPostResponse);
		return "post/post";
	}

	@GetMapping("/{simplePostId}/edit")
	public String getUpdatePost(@AuthUser User authUser, @PathVariable long simplePostId, Model model) {
		UpdatePostDto findPost = fullPostService.getFullPost(simplePostId, authUser.getId());
		model.addAttribute("UpdatePostDto", findPost);
		return "post/editPost";
	}

	@PostMapping("/{simplePostId}/edit")
	public String updatePost(@AuthUser User authUser, @PathVariable long simplePostId,
		@ModelAttribute UpdatePostDto updatePostDto) {
		fullPostService.updatePost(simplePostId, authUser.getId(), updatePostDto);
		return "post/post";
	}

	@GetMapping("/{simplePostId}/delete")
	public String deletePost(@AuthUser User authUser, @PathVariable long simplePostId) {
		fullPostService.deletePost(simplePostId, authUser.getId());
		return "post/myPosts";
	}

	@GetMapping("/{simplePostId}/end")
	public String closePost(@AuthUser User authUser, @PathVariable long simplePostId) {
		fullPostService.closePost(simplePostId, authUser.getId());
		return "post/post";
	}

}
