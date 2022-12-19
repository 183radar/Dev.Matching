package radar.devmatching.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import radar.devmatching.domain.user.entity.UserRole;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomUserSecurityContextFactory.class)
public @interface WithCustomUser {

	String username() default "CustomUserName";

	UserRole userRole() default UserRole.ROLE_USER;

}