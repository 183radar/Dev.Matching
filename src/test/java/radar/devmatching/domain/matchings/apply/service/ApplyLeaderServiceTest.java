package radar.devmatching.domain.matchings.apply.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.matchinguser.service.MatchingUserService;
import radar.devmatching.domain.post.simple.repository.SimplePostRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplyLeaderService의")
class ApplyLeaderServiceTest {

	@Mock
	ApplyRepository applyRepository;

	@Mock
	MatchingUserService matchingUserService;

	@Mock
	SimplePostRepository simplePostRepository;

	ApplyLeaderService applyLeaderService;

	@BeforeEach
	void setUp() {
		applyLeaderService = new ApplyLeaderServiceImpl(applyRepository, matchingUserService, simplePostRepository);
	}

}