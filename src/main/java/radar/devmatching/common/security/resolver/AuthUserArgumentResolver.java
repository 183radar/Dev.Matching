package radar.devmatching.common.security.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.security.JwtProperties;
import radar.devmatching.common.security.jwt.JwtTokenInfo;
import radar.devmatching.domain.user.service.UserService;

@Slf4j
@Component
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

	// TODO : AutoWired 수정
	@Autowired
	private UserService userService;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(JwtTokenInfo.class) &&
			parameter.hasParameterAnnotation(AuthUser.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Claims claims = (Claims)authentication.getPrincipal();
		Long userId = Long.parseLong((String)claims.get(JwtProperties.USER_ID));
		String username = (String)claims.get(JwtProperties.USERNAME);

		JwtTokenInfo tokenInfo = JwtTokenInfo.builder()
			.userId(userId)
			.username(username)
			.build();

		log.info("jwt token info={}", tokenInfo);

		return tokenInfo;
	}
}
