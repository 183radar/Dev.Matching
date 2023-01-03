package radar.devmatching.domain.user.controller;

import java.util.Objects;

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
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.security.jwt.JwtTokenInfo;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.UserService;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.request.UpdateUserRequest;
import radar.devmatching.domain.user.service.dto.response.UserResponse;

@Slf4j
@Controller
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
@SessionAttributes("createUserRequest")
public class UserController {

	private final UserService userService;

	@GetMapping(value = "/signUp")
	public String signUp(Model model) {
		model.addAttribute("createUserRequest", CreateUserRequest.of());
		return "redirect:/api/users/signUp/page";
	}

	@GetMapping(value = "/signUp/page")
	public String signUpPage(@ModelAttribute("createUserRequest") CreateUserRequest request, Model model) {
		model.addAttribute("createUserRequest", request);
		return "user/signUp";
	}

	/**
	 * TODO : 아이디, 닉네임 중복확인후 다른 아이디, 닉네임 요청하면 예외 던지기
	 */
	@PostMapping(value = "/signUp/page")
	public String signUpRequest(@Valid @ModelAttribute("creatUserRequest") CreateUserRequest request,
		BindingResult bindingResult, Model model, SessionStatus sessionStatus) {
		if (bindingResult.hasErrors()) {
			return "/user/signUp";
		}
		UserResponse user = userService.createUser(request);

		sessionStatus.setComplete();

		model.addAttribute("createUser", user);
		return "redirect:/api/users/signIn";
	}

	/**
	 * 요쳥 username 중복확인
	 * 중복 안되면 : 사용가능한 아이디입니다.
	 * 중복 되면 : 사용중인 아이디 입니다.
	 */
	@PostMapping("/duplicate/username")
	public String checkDuplicateUsername(@ModelAttribute("creatUserRequest") CreateUserRequest request, Model model,
		RedirectAttributes redirectAttributes) {
		userService.checkDuplicateUsername(request);
		redirectAttributes.addFlashAttribute("msg", request.getUsername() + ": 사용가능한 아이디 입니다.");
		model.addAttribute("createUserRequest", request);
		return "redirect:/api/users/signUp/page";
	}

	/**
	 * 요청 nickName 중복확인
	 * 중복 안되면 : 사용가능한 이름입니다.
	 * 중복 되면 : 사용중인 이름입니다.
	 */
	@PostMapping("/duplicate/nickName")
	public String checkDuplicateNickName(@ModelAttribute("creatUserRequest") CreateUserRequest request, Model model,
		RedirectAttributes redirectAttributes) {
		userService.checkDuplicateNickName(request);
		redirectAttributes.addFlashAttribute("msg", request.getNickName() + ": 사용가능한 이름 입니다.");
		model.addAttribute("createUserRequest", request);
		return "redirect:/api/users/signUp/page";
	}

	// TODO : 다른 사람이 접근할경우에는 다른 뷰를 사용
	@GetMapping
	public String getUser(@AuthUser JwtTokenInfo tokenInfo, Model model) {
		UserResponse user = userService.getUser(tokenInfo.getUserId());
		model.addAttribute("userInfo", user);
		return "user/userInfo";
	}

	@GetMapping("/{userId}")
	public String getSimpleUser(@PathVariable Long userId, @AuthUser User user, Model model) {
		if (Objects.equals(userId, user.getId())) {
			return "redirect:/api/users";
		}
		model.addAttribute("simpleUserInfo", userService.getSimpleUser(user));
		return "user/simpleUserInfo";
	}

	@GetMapping("/update")
	public String updateUser(@AuthUser JwtTokenInfo tokenInfo, Model model) {
		UserResponse user = userService.getUser(tokenInfo.getUserId());
		model.addAttribute("userInfo", user);
		return "user/userUpdate";
	}

	/**
	 * TODO : schoolname 빈칸으로 들어오면 valid 걸러내거나 exception 던져 처리하거나 고민중
	 */
	@PostMapping("/update")
	public String updateUser(@Valid @ModelAttribute UpdateUserRequest request, BindingResult bindingResult,
		@AuthUser JwtTokenInfo tokenInfo) {
		if (bindingResult.hasErrors()) {
			return "redirect:/api/users/update";
		}
		userService.updateUser(request, tokenInfo.getUserId());
		return "redirect:/api/users/";
	}

	/**
	 * batch 사용해서 한달뒤에 삭제하는 기능 추가할지 고민
	 * ex: user에 삭제 시간 설정해주고 한달뒤에 삭제
	 */
	@GetMapping("/delete")
	public String deleteUser(@AuthUser JwtTokenInfo jwtTokenInfo) {
		userService.deleteUser(jwtTokenInfo.getUserId());
		return "redirect:/api/users/signIn";
	}

	// TODO : 비밀번호 변경, 닉네임 변경 페이지 만들기.
}
