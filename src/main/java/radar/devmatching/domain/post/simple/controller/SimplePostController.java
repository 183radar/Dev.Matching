package radar.devmatching.domain.post.simple.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.post.simple.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/posts")
public class SimplePostController {

	private final SimplePostService simplePostService;

	@GetMapping("/new")
	public String getCreatePost(Model model) {
		model.addAttribute("createPostRequest", CreatePostRequest.of());
		return "post/createPost";
	}

	@PostMapping("/new")
	public String createPost(@AuthUser User authUser, @Valid @ModelAttribute CreatePostRequest createPostRequest,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "post/createPost";
		}
		long simplePostId = simplePostService.createPost(createPostRequest, authUser);
		return "redirect:/api/posts/" + simplePostId;
	}

	@GetMapping("/my")
	public String getMyPosts(@AuthUser User authUser, Model model) {
		List<SimplePostResponse> myPosts = simplePostService.getMyPosts(authUser.getId());
		model.addAttribute("myPosts", myPosts);
		return "post/myPosts";
	}

	@GetMapping("/applicationPosts")
	public String getApplicationPosts(@AuthUser User authUser, Model model) {
		List<SimplePostResponse> applicationPosts = simplePostService.getApplicationPosts(authUser.getId());
		model.addAttribute("applicationPosts", applicationPosts);
		return "post/applicationPosts";
	}

}
