package radar.devmatching.domain.user.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {
	ROLE_USER("유저"),
	ROLE_ADMIN("관리자");

	private final String name;

	public String getName() {
		return name;
	}
}
