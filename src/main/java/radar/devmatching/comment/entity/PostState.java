package radar.devmatching.comment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostState {
	RECRUITING("모집중"),
	END("완료");
	
	private final String name;
}
