package radar.devmatching.domain.comment.repository.custom;

import static radar.devmatching.domain.comment.entity.QComment.*;
import static radar.devmatching.domain.comment.entity.QMainComment.*;
import static radar.devmatching.domain.comment.entity.QSubComment.*;
import static radar.devmatching.domain.post.full.entity.QFullPost.*;
import static radar.devmatching.domain.post.simple.entity.QSimplePost.*;

import java.util.List;

import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;

import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.comment.entity.MainComment;

public class MainCommentRepositoryImpl implements MainCommentCustomRepository {

	private final JPAQueryFactory queryFactory;

	public MainCommentRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<MainComment> getAllComments(Long fullPostId) {
		return queryFactory
			.selectFrom(mainComment)
			.leftJoin(mainComment.comment, comment).fetchJoin()
			.leftJoin(mainComment.subComments, subComment).fetchJoin()
			.where(mainComment.fullPost.id.eq(fullPostId))
			.fetch();
	}

	@Override
	public Long findBySimplePostIdAsMainCommentId(Long mainCommentId) {
		Long findSimplePostId = queryFactory
			.select(simplePost.id)
			.from(simplePost)
			.join(simplePost.fullPost, fullPost)
			.join(fullPost.mainComments, mainComment)
			.where(mainComment.id.eq(mainCommentId))
			.fetchOne();

		if (findSimplePostId == null) {
			throw new EntityNotFoundException(ErrorMessage.MAIN_COMMENT_NOT_FOUND);
		}
		return findSimplePostId;
	}
}
