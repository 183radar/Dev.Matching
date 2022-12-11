package radar.devmatching.common.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import radar.devmatching.domain.user.entity.User;

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = -7280729607974805035L;
	private final User user;

	public CustomUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> list = new ArrayList<>();
		list.add(new SimpleGrantedAuthority(user.getUserRole().getName()));
		return list;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	/**
	 * UserDetails 구현
	 * 계정 만료 여부
	 *  true : 만료안됨
	 *  false : 만료됨
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
		// return false(Default)
	}

	/**
	 * UserDetails 구현
	 * 계정 잠김 여부
	 *  true : 잠기지 않음
	 *  false : 잠김
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
		// return false(Default)
	}

	/**
	 * UserDetails 구현
	 * 계정 비밀번호 만료 여부
	 *  true : 만료 안됨
	 *  false : 만료됨
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
		// return false(Default)
	}

	/**
	 * UserDetails 구현
	 * 계정 활성화 여부
	 *  true : 활성화됨
	 *  false : 활성화 안됨
	 */
	@Override
	public boolean isEnabled() {
		return true;
		// return false(Default)
	}

}
