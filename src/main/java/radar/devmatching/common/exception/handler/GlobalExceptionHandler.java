package radar.devmatching.common.exception.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.CustomAuthenticationException;
import radar.devmatching.common.exception.EntityNotFoundException;

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

	@ExceptionHandler(CustomAuthenticationException.class)
	public ModelAndView catchCustomAuthenticationException(CustomAuthenticationException e,
		HttpServletRequest request) {
		log.error(ERROR_MESSAGE, e);
		Map<String, Object> models = Map.of("status", e.getErrorMessage().getStatus().value());
		return errorViewResolver.resolveErrorView(request, e.getErrorMessage().getStatus(), models);
	}

	/**
	 * 나중에 validation 추가 시 field 에러 잡을 때 model에 값 넣고 redirect하는 작업들 추가돼야함.
	 */
}
