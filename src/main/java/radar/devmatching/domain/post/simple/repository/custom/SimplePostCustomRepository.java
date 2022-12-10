package radar.devmatching.domain.post.simple.repository.custom;

import java.util.List;

import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.dto.MainPostDto;

public interface SimplePostCustomRepository {

	List<SimplePost> findBySearchCondition(String postCategory, MainPostDto mainPostDto);
}
