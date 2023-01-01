package radar.devmatching.domain.matchings.matching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.matchings.matching.service.MatchingService;
import radar.devmatching.domain.matchings.matching.service.dto.MatchingUpdate;
import radar.devmatching.domain.matchings.matching.service.dto.response.MatchingResponse;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

	private final MatchingService matchingService;

	@GetMapping("/{matchingId}")
	public String getMatchingPage(@PathVariable("matchingId") Long matchingId, @AuthUser User user, Model model) {
		MatchingResponse matching = matchingService.getMatchingInfo(matchingId, user);
		model.addAttribute("matching", matching);
		return "matching/matching/matchingInfo";
	}

	/**
	 * TODO : 접근 사용자 검증 들어가야함
	 */
	@GetMapping("/{matchingId}/update")
	public String updateMatchingPage(@PathVariable Long matchingId, @AuthUser User user, Model model) {
		MatchingUpdate matchingUpdate = matchingService.getMatchingUpdateData(matchingId);
		model.addAttribute("matchingUpdate", matchingUpdate);
		return "matching/matching/matchingUpdate";
	}

	@PostMapping("/{matchingId}/update")
	public String updateMatching(@PathVariable Long matchingId, @AuthUser User user,
		@ModelAttribute("matchingUpdate") MatchingUpdate matchingUpdate) {
		matchingService.updateMatching(matchingId, matchingUpdate);
		return "redirect:/api/matching/" + matchingId;
	}

}
