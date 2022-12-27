package radar.devmatching.domain.post.simple.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostState {
	RECRUITING("모집중"),
	END("모집 완료");

	private final String name;
}
