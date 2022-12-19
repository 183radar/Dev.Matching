package radar.devmatching.domain.post.simple.repository.custom;

import static radar.devmatching.domain.post.simple.entity.QSimplePost.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import radar.devmatching.common.exception.InvalidParamException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.PostState;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.service.dto.MainPostDto;

public class SimplePostRepositoryImpl implements SimplePostCustomRepository {

	private final JPAQueryFactory queryFactory;

	public SimplePostRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<SimplePost> findRecruitingPostBySearchCondition(String postCategory, MainPostDto mainPostDto) {
		return queryFactory
			.selectFrom(simplePost)
			.where(categoryEq(postCategory),
				regionEq(mainPostDto.getRegion()),
				searchConditionContain(mainPostDto.getSearchCondition()),
				simplePost.postState.eq(PostState.RECRUITING))
			.orderBy(simplePost.createDate.desc())
			.fetch();
	}

	private BooleanExpression searchConditionContain(String searchCondition) {
		return StringUtils.hasText(searchCondition) ? simplePost.title.contains(searchCondition) : null;
	}

	private BooleanExpression regionEq(Region region) {
		return region != null ? simplePost.region.eq(region) : null;
	}

	private BooleanExpression categoryEq(String postCategory) {
		if (postCategory.equals("ALL")) {
			return null;
		}
		try {
			return simplePost.category.eq(PostCategory.valueOf(postCategory));
		} catch (IllegalArgumentException e) {
			throw new InvalidParamException(ErrorMessage.INVALID_POST_CATEGORY, e);
		}
	}
}
