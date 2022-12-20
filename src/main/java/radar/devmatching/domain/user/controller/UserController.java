package radar.devmatching.domain.user.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.exception.DuplicateException;
import radar.devmatching.domain.user.service.UserService;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.request.UpdateUserRequest;
import radar.devmatching.domain.user.service.dto.response.UserResponse;

@Controller
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
@SessionAttributes("createUserRequest")
public class UserController {

	private final UserService userService;

	@GetMapping(value = "/signUp")
	public String signUpPage(Model model) {
		model.addAttribute("createUserRequest", CreateUserRequest.of());
		return "/";
	}

	/**
	 * TODO : DuplicateException 던져지
	 * username, nickName 중복 확인 기능 추가 해야함
	 *
	 * TODO : html 만들때 hidden 테그로 usernameCheck = true, nickNameCheck = true가 되어야지만 생성이 되게 만들기
	 */
	@PostMapping(value = "/signup")
	public String signUpRequest(@Valid @ModelAttribute("creatUserRequest") CreateUserRequest request,
		BindingResult bindingResult,
		Model model, SessionStatus sessionStatus) {
		if (bindingResult.hasErrors()) {
			return "/";
		}
		UserResponse user = userService.createUser(request);

		sessionStatus.setComplete();

		model.addAttribute("createUser", user);
		return "/";
	}

	/**
	 * /api/users/duplicate/username?username=test
	 * 요쳥 username 중복확인
	 * 중복 안되면 : 사용가능한 아이디입니다.
	 * 중복 되면 : 사용중인 아이디 입니다.
	 *
	 * 중복 확인 후에 전에 있던 정보 다시 넘겨주기
	 */
	@GetMapping("/duplicate/username")
	public String checkDuplicateUsername(@ModelAttribute("creatUserRequest") CreateUserRequest request,
		RedirectAttributes redirectAttributes) {

		try {
			userService.checkDuplicateUsername(request.getUsername());
			request.usernameNonDuplicate();
			redirectAttributes.addAttribute("msg", request.getUsernameCheck() + ": 사용가능한 아이디 입니다.");
		} catch (DuplicateException e) {
			redirectAttributes.addAttribute("msg", request.getUsernameCheck() + ": 사용중인 아이디 입니다.");
			return "redirect:/api/users/duplicate/username";
		}
		return "redirect:/api/users/signUp";
	}

	/**
	 * /api/users/duplicate/nickName?nickName=test
	 * 요청 nickName 중복확인
	 * 중복 안되면 : 사용가능한 이름입니다.
	 * 중복 되면 : 사용중인 이름입니다.
	 */
	@GetMapping("/duplicate/nickName")
	public String checkDuplicateNickName(@ModelAttribute("creatUserRequest") CreateUserRequest request,
		RedirectAttributes redirectAttributes) {

		try {
			userService.checkDuplicateNickName(request.getNickName(), null);
			request.nickNameNonDuplicate();
			redirectAttributes.addAttribute("msg", request.getUsernameCheck() + ": 사용가능한 이름 입니다.");
		} catch (DuplicateException e) {
			redirectAttributes.addAttribute("msg", request.getUsernameCheck() + ": 사용중인 이름 입니다.");
			return "redirect:/api/users/duplicate/nickName";
		}
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
	public String updateUser(@Valid @ModelAttribute UpdateUserRequest request, BindingResult bindingResult,
		@PathVariable(name = "userId") Long requestUserId, @AuthUser User authUser) {
		if (bindingResult.hasErrors()) {
			return "/";
		}
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
