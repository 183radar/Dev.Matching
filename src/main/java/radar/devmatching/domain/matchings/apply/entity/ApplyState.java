package radar.devmatching.domain.matchings.apply.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplyState {
	DENIED("거절됨"),
	ACCEPTED("승인됨"),
	WAITING("승인 대기중");

	private final String state;
}
