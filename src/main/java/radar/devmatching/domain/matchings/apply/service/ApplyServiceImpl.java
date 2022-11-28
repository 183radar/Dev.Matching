package radar.devmatching.domain.matchings.apply.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.matchings.apply.entity.Apply;
import radar.devmatching.domain.matchings.apply.entity.ApplyState;
import radar.devmatching.domain.matchings.apply.repository.ApplyRepository;
import radar.devmatching.domain.matchings.apply.service.dto.response.ApplyResponse;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.repository.SimplePostRepository;
import radar.devmatching.domain.user.entity.User;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

	private final ApplyRepository applyRepository;
	private final SimplePostRepository simplePostRepository;

	@Override
	@Transactional
	public void createApply(Long simplePostId, User authUser) {
		SimplePost simplePost = simplePostRepository.findById(simplePostId)
			.orElseThrow(() -> new RuntimeException("not exist simplePost entity"));

		simplePost.getApplyList().stream()
			.filter(apply -> Objects.equals(apply.getApplyUser().getId(), authUser.getId()))
			.findAny()
			.ifPresent(apply -> {
				throw new RuntimeException("already apply");
			});

		Apply apply = Apply.builder().applyUser(authUser).applySimplePost(simplePost).build();

		applyRepository.save(apply);
	}

	@Override
	public List<ApplyResponse> getAllApplyList(User authUser, Long userId) {
		if (!Objects.equals(authUser.getId(), userId)) {
			throw new RuntimeException("Invalid access");
		}

		return authUser.getApplyList().stream()
			.map(ApplyResponse::of)
			.collect(Collectors.toList());
	}

	@Override
	public int getAcceptedApplyCount(Long simplePostId) {
		long count = applyRepository.findAllByApplySimplePostId(simplePostId).stream()
			.filter(apply -> Objects.equals(apply.getApplyState(), ApplyState.ACCEPTED))
			.count();
		return Math.toIntExact(count);
	}

}
