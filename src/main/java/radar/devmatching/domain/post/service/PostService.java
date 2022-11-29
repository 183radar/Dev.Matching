package radar.devmatching.domain.post.service;

import java.util.List;

import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.service.dto.request.UpdatePostRequest;
import radar.devmatching.domain.post.service.dto.response.PresentPostResponse;
import radar.devmatching.domain.post.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;

public interface PostService {

	SimplePost createPost(CreatePostRequest createPostDto, User user);

	List<SimplePostResponse> getMyPosts(long userId);

	List<SimplePostResponse> getApplicationPosts(long userId);

	SimplePost getSimplePostOnly(long simplePostId);

	PresentPostResponse getPostWithComment(long simplePostId);

	UpdatePostRequest getFullPost(long simplePostId, long leaderId);

	void updatePost(long simplePostId, UpdatePostRequest updatePostRequest);
}
