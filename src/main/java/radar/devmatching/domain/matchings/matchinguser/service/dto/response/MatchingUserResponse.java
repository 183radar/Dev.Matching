package radar.devmatching.domain.matchings.matchinguser.service.dto.response;

import lombok.Builder;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUserRole;
import radar.devmatching.domain.post.simple.entity.PostState;

public class MatchingUserResponse {

	private final Long matchingId;
	private final MatchingUserRole matchingUserRole;
	private final String matchingTitle;
	private final int userCount;
	private final PostState postState;

	@Builder
	public MatchingUserResponse(Long matchingId, MatchingUserRole matchingUserRole,
		String matchingTitle, int userCount, PostState postState) {
		this.matchingId = matchingId;
		this.matchingUserRole = matchingUserRole;
		this.matchingTitle = matchingTitle;
		this.userCount = userCount;
		this.postState = postState;
	}

	// TODO : 리팩터링
	public static MatchingUserResponse of(MatchingUser matchingUser) {
		return MatchingUserResponse.builder()
			.matchingId(matchingUser.getMatching().getId())
			.matchingUserRole(matchingUser.getMatchingUserRole())
			.matchingTitle(matchingUser.getMatching().getMatchingTitle())
			.userCount(matchingUser.getMatching().getMatchingUsers().size())
			.postState(matchingUser.getMatching().getSimplePost().getPostState())
			.build();
	}

	public Long getMatchingId() {
		return matchingId;
	}

	public MatchingUserRole getMatchingUserRole() {
		return matchingUserRole;
	}

	public String getMatchingTitle() {
		return matchingTitle;
	}

	public int getUserCount() {
		return userCount;
	}

	public PostState getPostState() {
		return postState;
	}
}
