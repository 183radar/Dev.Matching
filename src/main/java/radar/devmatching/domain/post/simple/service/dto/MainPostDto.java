package radar.devmatching.domain.post.simple.service.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.validator.constraints.Length;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MainPostDto {

	private String userName;

	private Region region;

	@Length(max = 20)
	private String searchCondition;

	private List<SimplePostResponse> simplePostResponses;

	@Builder
	public MainPostDto(String userName, Region region, String searchCondition,
		List<SimplePostResponse> simplePostResponses) {
		this.userName = userName;
		this.region = region;
		this.searchCondition = searchCondition;
		this.simplePostResponses = simplePostResponses;
	}

	public static MainPostDto of(String userName, Region region, List<SimplePost> simplePosts) {
		return MainPostDto.builder()
			.userName(userName)
			.region(region)
			.simplePostResponses(simplePosts.stream()
				.map(SimplePostResponse::of).
				collect(Collectors.toList()))
			.build();
	}

	public String getUserName() {
		return userName;
	}

	public Region getRegion() {
		return region;
	}

	public String getSearchCondition() {
		return searchCondition;
	}

	public List<SimplePostResponse> getSimplePostResponses() {
		return simplePostResponses;
	}
}
