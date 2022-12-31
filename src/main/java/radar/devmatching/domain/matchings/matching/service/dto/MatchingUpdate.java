package radar.devmatching.domain.matchings.matching.service.dto;

import lombok.Builder;
import radar.devmatching.domain.matchings.matching.entity.Matching;

public class MatchingUpdate {

	private Long matchingId;
	private String matchingTitle;
	private String matchingInfo;

	@Builder
	public MatchingUpdate(Long matchingId, String matchingTitle, String matchingInfo) {
		this.matchingId = matchingId;
		this.matchingTitle = matchingTitle;
		this.matchingInfo = matchingInfo;
	}

	public static MatchingUpdate of(Matching matching) {
		return MatchingUpdate.builder()
			.matchingId(matching.getId())
			.matchingTitle(matching.getMatchingTitle())
			.matchingInfo(matching.getMatchingInfo())
			.build();
	}

	public static MatchingUpdate of() {
		return MatchingUpdate.builder().build();
	}

	@Override
	public String toString() {
		return "MatchingUpdate{" +
			"matchingId=" + matchingId +
			", matchingTitle='" + matchingTitle + '\'' +
			", matchingInfo='" + matchingInfo + '\'' +
			'}';
	}

	public Long getMatchingId() {
		return matchingId;
	}

	public void setMatchingId(Long matchingId) {
		this.matchingId = matchingId;
	}

	public String getMatchingTitle() {
		return matchingTitle;
	}

	public void setMatchingTitle(String matchingTitle) {
		this.matchingTitle = matchingTitle;
	}

	public String getMatchingInfo() {
		return matchingInfo;
	}

	public void setMatchingInfo(String matchingInfo) {
		this.matchingInfo = matchingInfo;
	}

}
