package radar.devmatching.domain.comment.service.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.comment.entity.MainComment;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MainCommentDto {

	private CommentDto mainCommentDto;

	private List<CommentDto> subCommentDtos;

	@Builder(access = AccessLevel.PRIVATE)
	private MainCommentDto(CommentDto mainCommentDto, List<CommentDto> subCommentDtos) {
		this.mainCommentDto = mainCommentDto;
		this.subCommentDtos = subCommentDtos;
	}

	public static MainCommentDto of(MainComment mainComment) {
		return MainCommentDto.builder()
			.mainCommentDto(CommentDto.of(mainComment.getComment()))
			.subCommentDtos(
				mainComment.getSubComments()
					.stream()
					.map(subComment -> CommentDto.of(subComment.getComment()))
					.collect(Collectors.toList())
			).build();
	}
}
