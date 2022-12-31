package radar.devmatching.domain.matchings.matchinguser.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserService;
import radar.devmatching.domain.matchings.matchinguser.service.dto.response.MatchingUserResponse;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingUserController {

	private final MatchingUserService matchingUserService;

	@GetMapping("/list")
	public String getMatchingList(@AuthUser User user, Model model) {
		List<MatchingUserResponse> matchingUserList = matchingUserService.getMatchingUserList(user.getId());
		model.addAttribute("matchingUserList", matchingUserList);
		return "matching/matching/matchingList";
	}

}
