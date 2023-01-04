package radar.devmatching.domain.post.simple.service;

import java.util.List;

import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.dto.MainPostDto;
import radar.devmatching.domain.post.simple.service.dto.request.CreatePostRequest;
import radar.devmatching.domain.post.simple.service.dto.response.SimplePostResponse;

public interface SimplePostService {

	long createPost(CreatePostRequest createPostDto, long loginUserId);

	List<SimplePostResponse> getMyPosts(long loginUserId);

	List<SimplePostResponse> getApplicationPosts(long loginUserId);

	SimplePost findById(long simplePostId);

	SimplePost findPostById(long simplePostId);

	void deleteById(long simplePostId);

	MainPostDto getMainPostDto(long loginUserId, String postCategory);

	MainPostDto searchSimplePost(long loginUserId, String postCategory, MainPostDto mainPostDto);
}
