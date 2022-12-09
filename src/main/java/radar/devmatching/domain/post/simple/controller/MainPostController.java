package radar.devmatching.domain.post.simple.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
public class MainPostController {

	@GetMapping("/")
	public String getMainPage(@AuthUser User authUser, Model model) {

		return "main";
	}
}
