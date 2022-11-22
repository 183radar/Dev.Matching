package radar.devmatching.domain.matchings.matchinguser.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchingUserRole {
	LEADER("방장"),
	USER("유저");

	private final String name;
}
