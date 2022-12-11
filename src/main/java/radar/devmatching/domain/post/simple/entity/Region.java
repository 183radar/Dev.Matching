package radar.devmatching.domain.post.simple.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Region {
	SEOUL("서울"),
	SUWON("수원"),
	INCHEON("인천"),
	DAEGU("대구"),
	BUSAN("부산"),
	ULSAN("울산"),
	GWANGJU("광주"),
	JEONJU("전주"),
	JEJU("제주"),
	ETC("기타");

	private final String name;
}
