package radar.devmatching.domain.comment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostCategory {
	PROJECT("프로젝트"),
	MOGAKKO("모각코"),
	STUDY("스터디");

	private final String name;

}
