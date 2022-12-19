package radar.devmatching.domain.post.full.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.matchings.apply.service.ApplyService;
import radar.devmatching.domain.post.simple.repository.SimplePostRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("FullPostServiceImple 클래스의")
class FullPostServiceImplTest {

	@Mock
	private SimplePostRepository simplePostRepository;
	@Mock
	private ApplyService applyService;
	@Mock
	private CommentService commentService;

}