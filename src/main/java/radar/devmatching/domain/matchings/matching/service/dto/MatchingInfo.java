package radar.devmatching.domain.matchings.matching.service.dto;

import java.util.List;

import lombok.Builder;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUserRole;
import radar.devmatching.domain.post.simple.entity.PostState;

public class MatchingInfo {

	private final String matchingTitle;
	private final MatchingUserRole matchingUserRole;
	private final int userCount;
	private final PostState postState;
	private final String matchingInfo;
	private final List<MatchingUser> matchingUserList;

	@Builder
	public MatchingInfo(String matchingTitle, MatchingUserRole matchingUserRole, int userCount, PostState postState,
		String matchingInfo, List<MatchingUser> matchingUserList) {
		this.matchingTitle = matchingTitle;
		this.matchingUserRole = matchingUserRole;
		this.userCount = userCount;
		this.postState = postState;
		this.matchingInfo = matchingInfo;
		this.matchingUserList = matchingUserList;
	}

	// TODO : 리팩터링
	public static MatchingInfo of(MatchingUser matchingUser) {
		Matching matching = matchingUser.getMatching();

		return MatchingInfo.builder()
			.matchingTitle(matching.getMatchingTitle())
			.matchingUserRole(matchingUser.getMatchingUserRole())
			.userCount(matching.getMatchingUsers().size())
			.postState(matching.getSimplePost().getPostState())
			.matchingInfo(matching.getMatchingInfo())
			.matchingUserList(matching.getMatchingUsers())
			.build();
	}

	public String getMatchingTitle() {
		return matchingTitle;
	}

	public MatchingUserRole getMatchingUserRole() {
		return matchingUserRole;
	}

	public int getUserCount() {
		return userCount;
	}

	public PostState getPostState() {
		return postState;
	}

	public String getMatchingInfo() {
		return matchingInfo;
	}

	public List<MatchingUser> getMatchingUserList() {
		return matchingUserList;
	}
}
