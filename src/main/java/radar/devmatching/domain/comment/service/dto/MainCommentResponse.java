package radar.devmatching.domain.comment.service.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Builder;
import radar.devmatching.domain.comment.entity.MainComment;

@Builder(access = AccessLevel.PRIVATE)
public class MainCommentResponse {

	private final CommentResponse mainCommentResponse;

	private final List<CommentResponse> subCommentResponses;

	public static MainCommentResponse of(MainComment mainComment) {
		return MainCommentResponse.builder()
			.mainCommentResponse(CommentResponse.of(mainComment.getComment()))
			.subCommentResponses(
				mainComment.getSubComments()
					.stream()
					.map(subComment -> CommentResponse.of(subComment.getComment()))
					.collect(Collectors.toList())
			).build();
	}

	public CommentResponse getMainCommentResponse() {
		return mainCommentResponse;
	}

	public List<CommentResponse> getSubCommentResponses() {
		return subCommentResponses;
	}
}
