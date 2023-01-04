package radar.devmatching.domain.post.full.service;

import radar.devmatching.domain.post.full.service.dto.UpdatePostDto;
import radar.devmatching.domain.post.full.service.dto.response.PresentPostResponse;

public interface FullPostService {

	PresentPostResponse getPostWithComment(long simplePostId, long loginUserId);

	UpdatePostDto getUpdateFullPost(long simplePostId, long userId);

	void updatePost(long simplePostId, long userId, UpdatePostDto updatePostDto);

	void deletePost(long simplePostId, long userId);

	void closePost(long simplePostId, long userId);
}
