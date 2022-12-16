package radar.devmatching.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import radar.devmatching.common.security.CustomUserDetails;
import radar.devmatching.common.security.jwt.JwtAuthenticationToken;
import radar.devmatching.domain.user.entity.User;

public class WithCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithCustomUser customUser) {

		User user = User.builder()
			.username(customUser.username())
			.build();
		user.changeUserRole(customUser.userRole());
		UserDetails principal = new CustomUserDetails(user);

		Authentication auth = new JwtAuthenticationToken(principal, "", principal.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(auth);
		return context;
	}

}
