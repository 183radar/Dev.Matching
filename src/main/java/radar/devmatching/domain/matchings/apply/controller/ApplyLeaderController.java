package radar.devmatching.domain.matchings.apply.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.matchings.apply.service.ApplyLeaderService;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyLeaderResponse;
import radar.devmatching.domain.user.entity.User;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/posts")
public class ApplyLeaderController {

	private final ApplyLeaderService applyLeaderService;

	@GetMapping(value = "/{simplePostId}/apply/users")
	public String getApplyListInPost(@PathVariable(name = "simplePostId") Long simplePostId, @AuthUser User authUser,
		Model model) {
		List<ApplyLeaderResponse> applyList = applyLeaderService.getAllApplyList(authUser, simplePostId);
		model.addAttribute("applyList", applyList);
		return "matching/apply/applyLeaderList";
	}

	@GetMapping(value = "/{simplePostId}/apply/users/{applyId}/accept")
	public String updateApplyAccept(@PathVariable(name = "simplePostId") Long simplePostId,
		@PathVariable(name = "applyId") Long applyId, @AuthUser User authUser) {
		applyLeaderService.acceptApply(authUser, applyId, simplePostId);
		return "redirect:/api/posts/{simplePostId}/apply/users";
	}

	@GetMapping(value = "/{simplePostId}/apply/users/{applyId}/deny")
	public String updateApplyDeny(@PathVariable(name = "simplePostId") Long simplePostId,
		@PathVariable(name = "applyId") Long applyId, @AuthUser User authUser) {
		applyLeaderService.denyApply(authUser, applyId, simplePostId);
		return "redirect:/api/posts/{simplePostId}/apply/users";
	}
}
