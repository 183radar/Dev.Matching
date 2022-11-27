package radar.devmatching.domain.comment.repository.custom;

import static radar.devmatching.domain.comment.entity.QComment.*;
import static radar.devmatching.domain.comment.entity.QMainComment.*;
import static radar.devmatching.domain.comment.entity.QSubComment.*;

import java.util.List;

import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;

import radar.devmatching.domain.comment.entity.MainComment;

public class MainCommentRepositoryImpl implements MainCommentCustomRepository {

	private final JPAQueryFactory queryFactory;

	public MainCommentRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<MainComment> getAllComments(long fullPostId) {
		return queryFactory
			.selectFrom(mainComment)
			.leftJoin(mainComment.comment, comment).fetchJoin()
			.leftJoin(mainComment.subComments, subComment).fetchJoin()
			.where(mainComment.fullPost.id.eq(fullPostId))
			.fetch();
	}
}
