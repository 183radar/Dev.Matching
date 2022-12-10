package radar.devmatching.domain.post.simple.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.post.simple.service.dto.MainPostDto;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
public class MainPostController {

	private static final String categoryDefaultValue = "ALL";
	private final SimplePostService simplePostService;

	@GetMapping("/")
	public String getMainPage(@AuthUser User authUser, Model model,
		@RequestParam(value = "postCategory", defaultValue = categoryDefaultValue) String postCategory) {
		MainPostDto mainPostDto = simplePostService.getMainPostDto(authUser, postCategory);
		model.addAttribute("mainPostDto", mainPostDto);
		return "main";
	}

	@PostMapping("/")
	public String searchMainPage(@AuthUser User authUser, @ModelAttribute MainPostDto mainPostDto,
		@RequestParam(value = "postCategory", defaultValue = categoryDefaultValue) String postCategory) {
		mainPostDto = simplePostService.searchSimplePost(authUser, postCategory, mainPostDto);
		return "main";
	}
}
