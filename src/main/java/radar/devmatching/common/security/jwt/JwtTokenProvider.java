package radar.devmatching.common.security.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.security.JwtProperties;
import radar.devmatching.common.security.jwt.exception.ExpiredAccessTokenException;
import radar.devmatching.common.security.jwt.exception.ExpiredRefreshTokenException;
import radar.devmatching.common.security.jwt.exception.InvalidTokenException;
import radar.devmatching.domain.user.entity.UserRole;

@Slf4j
@Component
public class JwtTokenProvider {

	private static final String BEARER_TYPE = "Bearer";

	private final long ACCESS_TOKEN_EXPIRE_TIME;
	private final long REFRESH_TOKEN_EXPIRE_TIME;

	private final Key key;

	public JwtTokenProvider(@Value("${jwt.access-token-expire-time}") long accessTime,
		@Value("${jwt.refresh-token-expire-time}") long refreshTime,
		@Value("${jwt.secret}") String secretKey) {
		this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
		this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	protected String createToken(String username, UserRole userRole, long tokenValid) {
		Claims claims = Jwts.claims().setSubject(username);

		claims.put(JwtProperties.ROLE, userRole);

		Date date = new Date();

		// TODO : accessToken, refreshToken 발행시간 차이 개선하기.
		return Jwts.builder()
			.setClaims(claims) // 토큰 발행 유저 정보
			.setIssuedAt(date) // 토큰 발행 시간
			.setExpiration(new Date(date.getTime() + tokenValid)) // 토큰 만료 시간
			.signWith(SignatureAlgorithm.HS512, key)
			.compact();// 알고리즘과 키 설정
	}

	public String createAccessToken(String username, UserRole userRole) {
		return createToken(username, userRole, ACCESS_TOKEN_EXPIRE_TIME);
	}

	public String createRefreshToken(String username, UserRole userRole) {
		return createToken(username, userRole, REFRESH_TOKEN_EXPIRE_TIME);
	}

	public String createNewAccessTokenFromRefreshToken(String refreshToken) {
		Claims claims = parseClaims(refreshToken);
		String username = claims.getSubject();
		UserRole role = (UserRole)claims.get(JwtProperties.ROLE);
		return createAccessToken(username, role);
	}

	public String createNewRefreshToken(String refreshToken) {
		Claims claims = parseClaims(refreshToken);
		String username = claims.getSubject();
		UserRole role = (UserRole)claims.get(JwtProperties.ROLE);
		return createRefreshToken(username, role);
	}

	// TODO : 쿠키 만료시간 조정 필요
	// 쿠키 maxAge는 초단위 설정이라 1000으로 나눈값으로 설정
	public long getExpireTime() {
		return REFRESH_TOKEN_EXPIRE_TIME / 1000;
	}

	// 토큰 값을 파싱하여 클레임에 담긴 이메일 값을 가져온다.
	public String getUserNameByToken(String token) {
		return parseClaims(token).getSubject();
	}

	public Authentication getAuthentication(String accessToken) {
		Claims claims = parseClaims(accessToken);

		if (claims.get(JwtProperties.ROLE) == null || !StringUtils.hasText(claims.get(JwtProperties.ROLE).toString())) {
			throw new RuntimeException("NOT_FOUND_AUTHORITY"); //유저권한없음
		}

		log.info("access claims : username={}, authority={}", claims.getSubject(), claims.get(JwtProperties.ROLE));

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(JwtProperties.ROLE).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		// 여기서 사용하는 User는 spring security가 구현한 UserDetails 이다.
		UserDetails principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	public void validAccessToken(String token) {
		try {
			parseClaims(token);
		} catch (ExpiredJwtException e) {
			throw new ExpiredAccessTokenException();
		} catch (Exception e) {
			throw new InvalidTokenException();
		}
	}

	public void validRefreshToken(String token) {
		try {
			parseClaims(token);
		} catch (ExpiredJwtException e) {
			throw new ExpiredRefreshTokenException();
		} catch (Exception e) {
			throw new InvalidTokenException();
		}
	}

	public Claims parseClaims(String accessToken) {
		return Jwts.parser()
			.setSigningKey(key)
			.parseClaimsJws(accessToken)
			.getBody();
	}

}
