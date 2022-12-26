package radar.devmatching.domain.comment.repository.custom;

import static radar.devmatching.domain.comment.entity.QMainComment.*;
import static radar.devmatching.domain.comment.entity.QSubComment.*;
import static radar.devmatching.domain.post.full.entity.QFullPost.*;
import static radar.devmatching.domain.post.simple.entity.QSimplePost.*;

import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class SubCommentRepositoryImpl implements SubCommentCustomRepository {

	private final JPAQueryFactory queryFactory;

	public SubCommentRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Long findBySimplePostIdAsSubCommentId(Long subCommentId) {
		Long findSimplePostId = queryFactory
			.select(simplePost.id)
			.from(simplePost)
			.join(simplePost.fullPost, fullPost)
			.join(fullPost.mainComments, mainComment)
			.join(mainComment.subComments, subComment)
			.where(subComment.id.eq(subCommentId))
			.fetchOne();
		return findSimplePostId;
	}
}
