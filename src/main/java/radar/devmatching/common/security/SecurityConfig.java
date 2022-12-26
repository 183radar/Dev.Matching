package radar.devmatching.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import radar.devmatching.common.security.jwt.JwtAuthenticationEntryPoint;
import radar.devmatching.common.security.jwt.JwtAuthenticationFilter;
import radar.devmatching.common.security.jwt.JwtTokenProvider;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.antMatchers("/h2-console/**")
			.antMatchers("/api/users/signIn")
			.antMatchers("/js/scripts.js");

	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf().disable()
			.headers(header -> {
				header.frameOptions().disable();
			})
			.rememberMe().disable()
			.logout().disable()
			.formLogin().disable()
			.headers().disable()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()

			.authorizeRequests(
				request -> request.anyRequest().authenticated()
			)

			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, jwtAuthenticationEntryPoint),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
