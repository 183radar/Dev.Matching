package radar.devmatching.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import radar.devmatching.common.security.resolver.AuthUserArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	// TODO : AutoWired 수정
	@Autowired
	AuthUserArgumentResolver authUserArgumentResolver;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(authUserArgumentResolver);
	}
}
