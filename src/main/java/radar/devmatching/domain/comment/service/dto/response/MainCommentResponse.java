package radar.devmatching.domain.comment.service.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Builder;
import radar.devmatching.domain.comment.entity.MainComment;

@Builder(access = AccessLevel.PRIVATE)
public class MainCommentResponse {

	private final CommentResponse commentResponse;

	private final List<CommentResponse> subCommentResponses;

	public static MainCommentResponse of(MainComment mainComment) {
		return MainCommentResponse.builder()
			.commentResponse(CommentResponse.of(mainComment.getComment(), mainComment.getId()))
			.subCommentResponses(
				mainComment.getSubComments()
					.stream()
					.map(subComment -> CommentResponse.of(subComment.getComment(), subComment.getId()))
					.collect(Collectors.toList())
			).build();
	}

	public CommentResponse getCommentResponse() {
		return commentResponse;
	}

	public List<CommentResponse> getSubCommentResponses() {
		return subCommentResponses;
	}
}
