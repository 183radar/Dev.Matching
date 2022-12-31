package radar.devmatching.domain.matchings.apply.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.matchings.apply.service.ApplyService;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyResponse;
import radar.devmatching.domain.user.entity.User;

@Controller
@RequiredArgsConstructor
public class ApplyController {

	private final ApplyService applyService;

	@GetMapping(value = "/api/posts/{simplePostId}/apply")
	public String createApply(@PathVariable(name = "simplePostId") Long simplePostId, @AuthUser User authUser) {
		applyService.createApply(simplePostId, authUser);
		return "redirect:/api/posts/" + simplePostId;
	}

	/**
	 * 사용자가 신청한 개시글 리스트 반환
	 * TODO : 신청한 순서대로 반환
	 */
	@GetMapping(value = "/api/users/apply")
	public String getApplyListInUser(@AuthUser User authUser, Model model) {
		List<ApplyResponse> applyList = applyService.getAllApplyList(authUser);
		model.addAttribute("applyList", applyList);
		return "/matching/apply/applyList";
	}

}
