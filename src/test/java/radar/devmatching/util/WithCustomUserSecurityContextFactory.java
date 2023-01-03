package radar.devmatching.util;

import java.util.ArrayList;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

import radar.devmatching.common.security.jwt.JwtAuthenticationToken;
import radar.devmatching.domain.user.entity.User;

public class WithCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithCustomUser customUser) {

		User user = User.builder()
			.username(customUser.username())
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);
		user.changeUserRole(customUser.userRole());
		// UserDetails principal = new CustomUserDetails(user);

		ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(customUser.userRole().toString()));

		Authentication auth = new JwtAuthenticationToken(customUser.username(), "", authorities);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(auth);
		return context;
	}

}
