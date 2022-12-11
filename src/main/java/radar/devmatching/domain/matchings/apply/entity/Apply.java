package radar.devmatching.domain.matchings.apply.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.common.entity.BaseEntity;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;

@Table(name = "APPLY")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Apply extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "apply_id")
	private Long id;

	@Enumerated(value = EnumType.STRING)
	private ApplyState applyState;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User applyUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "simple_post_id")
	private SimplePost applySimplePost;

	@Builder
	public Apply(User applyUser, SimplePost applySimplePost) {
		this.applyState = ApplyState.WAITING;
		this.applyUser = applyUser;
		applyUser.getApplyList().add(this);
		this.applySimplePost = applySimplePost;
		applySimplePost.getApplyList().add(this);
	}

	public void acceptApply() {
		this.applyState = ApplyState.ACCEPTED;
	}

	public void denyApply() {
		this.applyState = ApplyState.DENIED;
	}
}
