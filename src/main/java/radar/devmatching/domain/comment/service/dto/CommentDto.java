package radar.devmatching.domain.comment.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.user.service.dto.SimpleUserDto;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDto {

	private SimpleUserDto simpleUserDto;

	private String content;

	@Builder(access = AccessLevel.PRIVATE)
	private CommentDto(SimpleUserDto simpleUserDto, String content) {
		this.simpleUserDto = simpleUserDto;
		this.content = content;
	}

	public static CommentDto of(Comment comment) {
		return CommentDto.builder()
			.simpleUserDto(SimpleUserDto.of(comment.getUser()))
			.content(comment.getContent())
			.build();
	}

}
