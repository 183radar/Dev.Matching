package radar.devmatching.domain.post.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.post.service.PostService;
import radar.devmatching.domain.post.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/posts")
public class SimplePostController {

	private final PostService postService;

	@GetMapping("/new")
	public String getCreatePost(Model model) {
		model.addAttribute("createPostDto", CreatePostRequest.of());
		return "post/createPost";
	}

	@PostMapping("/new")
	public String createPost(@AuthUser User authUser, @ModelAttribute CreatePostRequest createPostRequest) {
		long simplePostId = postService.createPost(createPostRequest, authUser);
		return "redirect:/api/posts/" + simplePostId;
	}

	@GetMapping("/my")
	public String getMyPosts(@AuthUser User authUser, Model model) {
		List<SimplePostResponse> myPosts = postService.getMyPosts(authUser.getId());
		model.addAttribute("myPosts", myPosts);
		return "post/myPosts";
	}

	@GetMapping("/applicationPosts")
	public String getApplicationPosts(@AuthUser User authUser, Model model) {
		List<SimplePostResponse> applicationPosts = postService.getApplicationPosts(authUser.getId());
		model.addAttribute("applicationPosts", applicationPosts);
		return "post/applicationPosts";
	}

}
