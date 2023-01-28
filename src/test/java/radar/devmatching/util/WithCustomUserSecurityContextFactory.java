package radar.devmatching.util;

import java.util.ArrayList;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import radar.devmatching.common.security.JwtProperties;
import radar.devmatching.common.security.jwt.JwtAuthenticationToken;

public class WithCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithCustomUser customUser) {
		ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(customUser.userRole().toString()));

		Claims claims = new DefaultClaims();
		claims.put(JwtProperties.USER_ID, "1");
		claims.put(JwtProperties.USERNAME, "CustomUsername");

		JwtAuthenticationToken authentication = new JwtAuthenticationToken(claims, "", authorities);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		return context;
	}

}
