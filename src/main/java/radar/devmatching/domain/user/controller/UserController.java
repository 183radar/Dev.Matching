package radar.devmatching.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.UserService;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.request.UpdateUserRequest;
import radar.devmatching.domain.user.service.dto.response.UserResponse;

@Controller
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping(value = "/signUp")
	public String signUpPage() {
		return "/";
	}

	/**
	 * UserService에서 AuthService를 분리하면 컨트롤러도 분리 예정
	 */
	@PostMapping(value = "/signup")
	public String signUpRequest(@ModelAttribute CreateUserRequest request, Model model) {
		UserResponse user = userService.createUser(request);
		model.addAttribute("createUser", user);
		return "/";
	}

	@GetMapping("/{userId}")
	public String getUser(@PathVariable(name = "userId") Long requestUserId, @AuthUser User authUser, Model model) {
		UserResponse user = userService.getUser(requestUserId, authUser);
		model.addAttribute("user", user);
		return "/";
	}

	@GetMapping("/{userId}/update")
	public String updateUser(@PathVariable(name = "userId") Long requestUserId, @AuthUser User authUser, Model model) {
		UserResponse user = userService.getUser(requestUserId, authUser);
		model.addAttribute("user", user);
		return "/";
	}

	@PostMapping("/{userId}/update")
	public String updateUser(@ModelAttribute UpdateUserRequest request,
		@PathVariable(name = "userId") Long requestUserId, @AuthUser User authUser) {
		userService.updateUser(request, requestUserId, authUser);
		return "redirect:/api/users/" + requestUserId;
	}

	/**
	 * 회원삭제 기능에 경우 일정시간후에 삭제하는 기능 고민˜
	 */
	@GetMapping("/{userId}/delete")
	public String deleteUser(@PathVariable(name = "userId") Long requestUserId, @AuthUser User authUser) {
		userService.deleteUser(requestUserId, authUser);
		return "/";
	}
}
