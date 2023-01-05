package radar.devmatching.domain.post.simple.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.jwt.JwtTokenInfo;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.service.SimplePostService;
import radar.devmatching.domain.post.simple.service.dto.MainPostDto;

@Controller
@RequiredArgsConstructor
@SessionAttributes("regions")
public class MainPostController {

	private static final String categoryDefaultValue = "ALL";
	private final SimplePostService simplePostService;

	@ModelAttribute("regions")
	public Region[] regions() {
		return Region.values();
	}

	@GetMapping("/")
	public String getMainPage(@AuthUser JwtTokenInfo jwtTokenInfo, Model model,
		@RequestParam(value = "postCategory", defaultValue = categoryDefaultValue) String postCategory) {
		MainPostDto mainPostDto = simplePostService.getMainPostDto(jwtTokenInfo.getUserId(), postCategory);
		model.addAttribute("mainPostDto", mainPostDto);
		return "main";
	}

	@PostMapping("/")
	public String searchMainPage(@AuthUser JwtTokenInfo jwtTokenInfo, @Valid @ModelAttribute MainPostDto mainPostDto,
		BindingResult bindingResult, Model model,
		@RequestParam(value = "postCategory", defaultValue = categoryDefaultValue) String postCategory) {
		if (bindingResult.hasErrors()) {
			return "main";
		}
		mainPostDto = simplePostService.searchSimplePost(jwtTokenInfo.getUserId(), postCategory, mainPostDto);
		model.addAttribute("mainPostDto", mainPostDto);
		return "main";
	}
}
