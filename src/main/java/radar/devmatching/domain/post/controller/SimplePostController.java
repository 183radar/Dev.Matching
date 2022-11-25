package radar.devmatching.domain.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.post.service.PostService;
import radar.devmatching.domain.post.service.dto.CreatePostDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class SimplePostController {

	private final PostService postService;

	@GetMapping("/new")
	public String getCreatePost(Model model) {
		model.addAttribute("createPostDto", CreatePostDto.of());
		return "post/createPost";
	}

	@PostMapping("/new")
	public String createPost(@ModelAttribute CreatePostDto createPostDto) {
		// postService.createPost(createPostDto, user);
		return "post/post";
	}

	@GetMapping("/my")
	public String getMyPosts() {
		// List<SimplePost> myPosts = postService.getMyPost(loginUser.getId());
		return "post/myPosts";
	}

	@GetMapping("/applicationPosts")
	public String getApplicationPosts() {
		// postService.getApplicationPosts(user);
		return "post/applicationPosts";
	}

}
