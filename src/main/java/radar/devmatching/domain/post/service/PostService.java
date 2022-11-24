package radar.devmatching.domain.post.service;

import java.util.List;

import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.service.dto.CreatePostDto;
import radar.devmatching.domain.user.entity.User;

public interface PostService {

	SimplePost createPost(CreatePostDto createPostDto, User user);

	List<SimplePost> getMyPosts(long userId);

	List<SimplePost> findApplicationPost(long userId);
}
