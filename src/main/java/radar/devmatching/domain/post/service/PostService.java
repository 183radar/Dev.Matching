package radar.devmatching.domain.post.service;

import java.util.List;

import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.service.dto.UpdatePostDto;
import radar.devmatching.domain.post.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.service.dto.response.PresentPostResponse;
import radar.devmatching.domain.post.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;

public interface PostService {

	long createPost(CreatePostRequest createPostDto, User user);

	List<SimplePostResponse> getMyPosts(long userId);

	List<SimplePostResponse> getApplicationPosts(long userId);

	SimplePost getSimplePostOnly(long simplePostId);

	PresentPostResponse getPostWithComment(long simplePostId);

	UpdatePostDto getFullPost(long simplePostId, long leaderId);

	void updatePost(long simplePostId, long leaderId, UpdatePostDto updatePostDto);

	void deletePost(long simplePostId, long leaderId);

	void closePost(long simplePostId, long leaderId);
}
