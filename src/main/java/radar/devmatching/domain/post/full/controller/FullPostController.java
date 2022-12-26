package radar.devmatching.domain.post.full.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.post.full.service.FullPostService;
import radar.devmatching.domain.post.full.service.dto.UpdatePostDto;
import radar.devmatching.domain.post.full.service.dto.response.PresentPostResponse;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/posts")
@SessionAttributes({"regions", "categories"})
public class FullPostController {

	private final FullPostService fullPostService;

	@ModelAttribute("regions")
	public Region[] regions() {
		return Region.values();
	}

	@ModelAttribute("categories")
	public PostCategory[] categories() {
		return PostCategory.values();
	}

	@GetMapping("/{simplePostId}")
	public String getFullPost(@AuthUser User authUser, @PathVariable long simplePostId, Model model) {
		PresentPostResponse presentPostResponse = fullPostService.getPostWithComment(simplePostId, authUser);
		model.addAttribute("presentPostResponse", presentPostResponse);
		return "post/post";
	}

	@GetMapping("/{simplePostId}/edit")
	public String getUpdatePost(@AuthUser User authUser, @PathVariable long simplePostId, Model model) {
		UpdatePostDto findPost = fullPostService.getFullPost(simplePostId, authUser.getId());
		model.addAttribute("updatePostDto", findPost);
		return "post/editPost";
	}

	@PostMapping("/{simplePostId}/edit")
	public String updatePost(@AuthUser User authUser, @PathVariable long simplePostId,
		@Valid @ModelAttribute UpdatePostDto updatePostDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "post/editPost";
		}
		fullPostService.updatePost(simplePostId, authUser.getId(), updatePostDto);
		return "redirect:/api/posts/" + simplePostId;
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
