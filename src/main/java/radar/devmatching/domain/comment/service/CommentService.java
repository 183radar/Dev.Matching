package radar.devmatching.domain.comment.service;

import radar.devmatching.domain.comment.service.dto.CommentDto;

public interface CommentService {
	void createMainComment(long simplePostId, CommentDto commentDto);
}
