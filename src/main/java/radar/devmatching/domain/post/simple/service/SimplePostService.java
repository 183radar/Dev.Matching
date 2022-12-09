package radar.devmatching.domain.post.simple.service;

import java.util.List;

import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;
import radar.devmatching.domain.user.entity.User;

public interface SimplePostService {

	long createPost(CreatePostRequest createPostDto, User user);

	List<SimplePostResponse> getMyPosts(long userId);

	List<SimplePostResponse> getApplicationPosts(long userId);

	SimplePost getSimplePostOnly(long simplePostId);

}
