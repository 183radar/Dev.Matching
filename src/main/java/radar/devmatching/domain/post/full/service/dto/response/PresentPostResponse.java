package radar.devmatching.domain.post.full.service.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.comment.service.dto.response.MainCommentResponse;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.service.dto.response.SimpleUserResponse;

@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class PresentPostResponse {

	private SimplePostResponse simplePostResponse;

	private SimpleUserResponse loginUser;

	private SimpleUserResponse postLeader;

	private Integer applyCount;

	private boolean isAppliedLoginUser;

	private String content;

	private List<MainCommentResponse> mainCommentResponses;

	public static PresentPostResponse of(SimplePost simplePost, User loginUser, int applyCount,
		boolean isAppliedLoginUser,
		List<MainCommentResponse> mainCommentResponse) {
		return PresentPostResponse.builder()
			.simplePostResponse(SimplePostResponse.of(simplePost))
			.loginUser(SimpleUserResponse.of(loginUser))
			.postLeader(SimpleUserResponse.of(simplePost.getLeader()))
			.applyCount(applyCount)
			.isAppliedLoginUser(isAppliedLoginUser)
			.content(simplePost.getFullPost().getContent())
			.mainCommentResponses(mainCommentResponse)
			.build();
	}

	public SimplePostResponse getSimplePostResponse() {
		return simplePostResponse;
	}

	public SimpleUserResponse getPostLeader() {
		return postLeader;
	}

	public SimpleUserResponse getLoginUser() {
		return loginUser;
	}

	public Integer getApplyCount() {
		return applyCount;
	}

	public boolean isAppliedLoginUser() {
		return isAppliedLoginUser;
	}

	public String getContent() {
		return content;
	}

	public List<MainCommentResponse> getMainCommentResponses() {
		return mainCommentResponses;
	}
}
