package radar.devmatching.domain.matchings.matching.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.matchings.matching.repository.MatchingRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingServiceImpl {

	private final MatchingRepository matchingRepository;

}
