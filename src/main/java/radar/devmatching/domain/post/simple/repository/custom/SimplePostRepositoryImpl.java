package radar.devmatching.domain.post.simple.repository.custom;

import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class SimplePostRepositoryImpl implements SimplePostCustomRepository {

	private final JPAQueryFactory queryFactory;

	public SimplePostRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

}
