package radar.devmatching.domain.post.service;

import java.util.List;

import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.service.dto.CreatePostDto;
import radar.devmatching.domain.post.service.dto.PresentSimplePostDto;
import radar.devmatching.domain.post.service.dto.UpdatePostDto;
import radar.devmatching.domain.user.entity.User;

public interface PostService {

	SimplePost createPost(CreatePostDto createPostDto, User user);

	List<PresentSimplePostDto> getMyPosts(long userId);

	List<PresentSimplePostDto> getApplicationPosts(long userId);

	SimplePost getSimplePostOnly(long simplePostId);

	SimplePost getPost(long simplePostId);

	void updatePost(long simplePostId, UpdatePostDto updatePostDto);
}
