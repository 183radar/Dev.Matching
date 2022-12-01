package radar.devmatching.common.exception.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.BusinessException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final String ERROR_MESSAGE = "[ERROR] class = {} / message = {}";

	@ExceptionHandler(BusinessException.class)
	public void catchBusinessException(BusinessException e, HttpServletResponse response, Model model) throws
		IOException {
		log.error(ERROR_MESSAGE, e.getClass().getSimpleName(), e);
		response.setStatus(e.getErrorMessage().getStatus().value());
		// response.sendRedirect("/api/my");
		model.addAttribute("msg", e.getMessage());
	}
}
