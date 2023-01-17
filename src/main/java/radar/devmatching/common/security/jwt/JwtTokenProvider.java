package radar.devmatching.common.security.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.BusinessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.common.security.JwtProperties;
import radar.devmatching.common.security.jwt.exception.ExpiredAccessTokenException;
import radar.devmatching.common.security.jwt.exception.ExpiredRefreshTokenException;
import radar.devmatching.common.security.jwt.exception.InvalidTokenException;
import radar.devmatching.domain.user.entity.UserRole;

@Slf4j
@Component
public class JwtTokenProvider {

	private final String CLAIM_USER_ID = JwtProperties.USER_ID;
	private final String CLAIM_USERNAME = JwtProperties.USERNAME;
	private final String CLAIM_USER_ROLE = JwtProperties.ROLE;

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

	protected String createToken(Long userId, String username, UserRole userRole, long tokenValid) {

		Map<String, Object> header = new HashMap<>();
		header.put("typ", "JWT");

		Claims claims = Jwts.claims();

		claims.put(CLAIM_USER_ID, userId.toString());
		claims.put(CLAIM_USERNAME, username);
		claims.put(CLAIM_USER_ROLE, userRole);

		Date date = new Date();

		// TODO : accessToken, refreshToken 발행시간 차이 개선하기.
		return Jwts.builder()
			.setHeader(header)
			.setClaims(claims) // 토큰 발행 유저 정보
			.setIssuedAt(date) // 토큰 발행 시간
			.setExpiration(new Date(date.getTime() + tokenValid)) // 토큰 만료 시간
			.signWith(SignatureAlgorithm.HS512, key)
			.compact();// 알고리즘과 키 설정
	}

	public String createAccessToken(Long userId, String username, UserRole userRole) {
		return createToken(userId, username, userRole, ACCESS_TOKEN_EXPIRE_TIME);
	}

	public String createRefreshToken(Long userId, String username, UserRole userRole) {
		return createToken(userId, username, userRole, REFRESH_TOKEN_EXPIRE_TIME);
	}

	public String createNewAccessTokenFromRefreshToken(String refreshToken) {
		Claims claims = parseClaims(refreshToken);

		Long userId = Long.parseLong((String)claims.get(CLAIM_USER_ID));
		String username = (String)claims.get(CLAIM_USERNAME);
		UserRole role = UserRole.valueOf((String)claims.get(CLAIM_USER_ROLE));
		return createAccessToken(userId, username, role);
	}

	/**
	 * 쿠키 maxAge는 초단위 설정이라 1000으로 나눈값으로 설정
	 * 지금 설정은 1주일로 설정되어있음
	 */
	public long getExpireTime() {
		return REFRESH_TOKEN_EXPIRE_TIME / 1000;
	}

	public Authentication getAuthentication(String accessToken) {
		Claims claims = parseClaims(accessToken);
		log.info("user role={}", claims.get(CLAIM_USER_ROLE).toString());
		if (claims.get(CLAIM_USER_ROLE) == null || !StringUtils.hasText(claims.get(CLAIM_USER_ROLE).toString())) {
			throw new BusinessException(ErrorMessage.AUTHORITY_NOT_FOUND); //유저권한없음
		}

		log.info("access claims : username={}, authority={}", claims.getSubject(), claims.get(CLAIM_USER_ROLE));

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(CLAIM_USER_ROLE).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		// TODO : principal 을 생성할지 고민

		return new JwtAuthenticationToken(claims, "", authorities);
	}

	public void validAccessToken(String token) {
		try {
			parseClaims(token);
		} catch (ExpiredJwtException e) {
			throw new ExpiredAccessTokenException();
		} catch (Exception e) {
			throw new InvalidTokenException(e);
		}
	}

	public void validRefreshToken(String token) {
		try {
			parseClaims(token);
		} catch (ExpiredJwtException e) {
			throw new ExpiredRefreshTokenException();
		} catch (Exception e) {
			throw new InvalidTokenException(e);
		}
	}

	public Claims parseClaims(String accessToken) {
		return Jwts.parser()
			.setSigningKey(key)
			.parseClaimsJws(accessToken)
			.getBody();
	}

}
