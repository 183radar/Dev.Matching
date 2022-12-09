package radar.devmatching.domain.post.full.service;

import radar.devmatching.domain.post.simple.service.dto.UpdatePostDto;
import radar.devmatching.domain.post.simple.service.dto.response.PresentPostResponse;

public interface FullPostService {

	PresentPostResponse getPostWithComment(long simplePostId);

	UpdatePostDto getFullPost(long simplePostId, long leaderId);

	void updatePost(long simplePostId, long leaderId, UpdatePostDto updatePostDto);

	void deletePost(long simplePostId, long leaderId);

	void closePost(long simplePostId, long leaderId);
}
