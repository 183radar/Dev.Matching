package radar.devmatching.common.exception.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.UsernamePasswordNotMatchException;
import radar.devmatching.domain.user.exception.DuplicateException;
import radar.devmatching.domain.user.exception.EmptySpaceException;
import radar.devmatching.domain.user.service.dto.request.SignInRequest;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private static final String ERROR_MESSAGE = "[ERROR]";
	private final ErrorViewResolver errorViewResolver;

	@ExceptionHandler(EntityNotFoundException.class)
	public ModelAndView catchEntityNotFoundException(EntityNotFoundException e,
		HttpServletRequest request) {
		log.error(ERROR_MESSAGE, e);
		Map<String, Object> models = Map.of("status", e.getErrorMessage().getStatus().value());
		return errorViewResolver.resolveErrorView(request, e.getErrorMessage().getStatus(), models);
	}

	@ExceptionHandler(UsernamePasswordNotMatchException.class)
	public String catchUsernamePasswordNotMatchException(UsernamePasswordNotMatchException e,
		HttpServletRequest request, Model model) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		model.addAttribute("signInRequest", new SignInRequest(username, password));
		model.addAttribute("error", "이름과 비밀번호가 일치하지 않습니다.");
		return "user/signIn";
	}

	@ExceptionHandler(DuplicateException.class)
	public String catchDuplicateException(DuplicateException e, RedirectAttributes redirectAttributes) {
		switch (e.getErrorMessage()) {
			case DUPLICATE_USERNAME:
				redirectAttributes.addFlashAttribute("msg", "사용중인 아이디 입니다.");
				break;
			case DUPLICATE_NICKNAME:
				redirectAttributes.addFlashAttribute("msg", "사용중인 이름 입니다.");
				break;
		}
		return "redirect:/api/users/signUp/page";
	}

	@ExceptionHandler(EmptySpaceException.class)
	public String catchEmptySpaceException(EmptySpaceException e, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("msg", "문자를 입력해주세요.");
		return "redirect:/api/users/signUp/page";
	}

}
