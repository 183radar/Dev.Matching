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
import org.springframework.web.bind.annotation.SessionAttributes;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.jwt.JwtTokenInfo;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.post.simple.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/posts")
@SessionAttributes({"regions", "categories"})
public class SimplePostController {

	private final SimplePostService simplePostService;

	@ModelAttribute("regions")
	public Region[] regions() {
		return Region.values();
	}

	@ModelAttribute("categories")
	public PostCategory[] categories() {
		return PostCategory.values();
	}

	@GetMapping("/new")
	public String getCreatePost(Model model) {
		model.addAttribute("createPostRequest", CreatePostRequest.of());
		return "post/createPost";
	}

	@PostMapping("/new")
	public String createPost(@AuthUser JwtTokenInfo jwtTokenInfo,
		@Valid @ModelAttribute CreatePostRequest createPostRequest,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "post/createPost";
		}
		long simplePostId = simplePostService.createPost(createPostRequest, jwtTokenInfo.getUserId());
		return "redirect:/api/posts/" + simplePostId;
	}

	@GetMapping("/my")
	public String getMyPosts(@AuthUser JwtTokenInfo jwtTokenInfo, Model model) {
		List<SimplePostResponse> myPosts = simplePostService.getMyPosts(jwtTokenInfo.getUserId());
		model.addAttribute("myPosts", myPosts);
		return "post/myPosts";
	}

	@GetMapping("/applicationPosts")
	public String getApplicationPosts(@AuthUser JwtTokenInfo jwtTokenInfo, Model model) {
		List<SimplePostResponse> applicationPosts = simplePostService.getApplicationPosts(jwtTokenInfo.getUserId());
		model.addAttribute("applicationPosts", applicationPosts);
		return "post/applicationPosts";
	}

}
