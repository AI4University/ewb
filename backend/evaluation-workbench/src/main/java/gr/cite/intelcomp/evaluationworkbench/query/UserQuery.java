package gr.cite.intelcomp.evaluationworkbench.query;

import gr.cite.commons.web.authz.service.AuthorizationService;

import gr.cite.intelcomp.evaluationworkbench.authorization.AuthorizationFlags;
import gr.cite.intelcomp.evaluationworkbench.common.enums.IsActive;
import gr.cite.intelcomp.evaluationworkbench.common.scope.user.UserScope;
import gr.cite.intelcomp.evaluationworkbench.data.UserEntity;
import gr.cite.intelcomp.evaluationworkbench.data.UserRoleEntity;
import gr.cite.intelcomp.evaluationworkbench.model.User;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserQuery extends QueryBase<UserEntity> {
	private String like;
	private Collection<UUID> ids;
	private Collection<String> subjectIds;
	private Collection<UUID> excludedIds;
	private Collection<IsActive> isActives;
	private UserRoleQuery userRoleQuery;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	private final UserScope userScope;
	private final AuthorizationService authService;
	public UserQuery(UserScope userScope, AuthorizationService authService) {
		this.userScope = userScope;
		this.authService = authService;
	}

	public UserQuery like(String value) {
		this.like = value;
		return this;
	}

	public UserQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public UserQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public UserQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public UserQuery subjectIds(String value) {
		this.subjectIds = List.of(value);
		return this;
	}

	public UserQuery subjectIds(String... value) {
		this.subjectIds = Arrays.asList(value);
		return this;
	}

	public UserQuery subjectIds(Collection<String> values) {
		this.subjectIds = values;
		return this;
	}

	public UserQuery excludedIds(Collection<UUID> values) {
		this.excludedIds = values;
		return this;
	}

	public UserQuery excludedIds(UUID value) {
		this.excludedIds = List.of(value);
		return this;
	}

	public UserQuery excludedIds(UUID... value) {
		this.excludedIds = Arrays.asList(value);
		return this;
	}

	public UserQuery isActive(IsActive value) {
		this.isActives = List.of(value);
		return this;
	}

	public UserQuery isActive(IsActive... value) {
		this.isActives = Arrays.asList(value);
		return this;
	}

	public UserQuery isActive(Collection<IsActive> values) {
		this.isActives = values;
		return this;
	}

	public UserQuery userRoleSubQuery(UserRoleQuery userRoleSubQuery) {
		this.userRoleQuery = userRoleSubQuery;
		return this;
	}


	public UserQuery enableTracking() {
		this.noTracking = false;
		return this;
	}

	public UserQuery disableTracking() {
		this.noTracking = true;
		return this;
	}

	public UserQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	protected Boolean isFalseQuery() {
		return
				this.isEmpty(this.ids) ||
						this.isEmpty(this.isActives) ||
						this.isEmpty(this.subjectIds) ||
						this.isEmpty(this.excludedIds);
	}

	@Override
	protected Class<UserEntity> entityClass() {
		return UserEntity.class;
	}

	@Override
	protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
		List<Predicate> predicates = new ArrayList<>();
		if (this.like != null && !this.like.isEmpty()) {
			predicates.add(queryContext.CriteriaBuilder.or(queryContext.CriteriaBuilder.like(queryContext.Root.get(UserEntity._firstName), this.like),
					queryContext.CriteriaBuilder.like(queryContext.Root.get(UserEntity._lastName), this.like)
			));
		}
		if (this.ids != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserEntity._id));
			for (UUID item : this.ids)
				inClause.value(item);
			predicates.add(inClause);
		}
		if (this.subjectIds != null) {
			CriteriaBuilder.In<String> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserEntity._subjectId));
			for (String item : this.subjectIds) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.excludedIds != null) {
			CriteriaBuilder.In<UUID> notInClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserEntity._id));
			for (UUID item : this.excludedIds)
				notInClause.value(item);
			predicates.add(notInClause.not());
		}
		if (this.isActives != null) {
			CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserEntity._isActive));
			for (IsActive item : this.isActives)
				inClause.value(item);
			predicates.add(inClause);
		}
		if (this.userRoleQuery != null) {
			QueryContext<UserRoleEntity, UUID> subQuery = this.applySubQuery(this.userRoleQuery, queryContext, UUID.class, userRoleEntityRoot -> userRoleEntityRoot.get(UserRoleEntity._userId));
			predicates.add(queryContext.CriteriaBuilder.in(queryContext.Root.get(UserEntity._id)).value(subQuery.Query));
		}


		if (!predicates.isEmpty()) {
			Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
			return queryContext.CriteriaBuilder.and(predicatesArray);
		} else {
			return null;
		}
	}

	@Override
	protected String fieldNameOf(FieldResolver item) {
		if (item.match(User._id)) return UserEntity._id;
		else if (item.match(User._firstName)) return UserEntity._firstName;
		else if (item.match(User._lastName)) return UserEntity._lastName;
		else if (item.match(User._timezone)) return UserEntity._timezone;
		else if (item.match(User._culture)) return UserEntity._culture;
		else if (item.match(User._language)) return UserEntity._language;
		else if (item.match(User._subjectId)) return UserEntity._subjectId;
		else if (item.match(User._isActive)) return UserEntity._isActive;
		else if (item.match(User._createdAt)) return UserEntity._createdAt;
		else if (item.match(User._updatedAt)) return UserEntity._updatedAt;
		else if (item.match(User._hash)) return UserEntity._updatedAt;
		else return null;
	}

	@Override
	protected UserEntity convert(Tuple tuple, Set<String> columns) {
		UserEntity item = new UserEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, UserEntity._id, UUID.class));
		item.setFirstName(QueryBase.convertSafe(tuple, columns, UserEntity._firstName, String.class));
		item.setLastName(QueryBase.convertSafe(tuple, columns, UserEntity._lastName, String.class));
		item.setTimezone(QueryBase.convertSafe(tuple, columns, UserEntity._timezone, String.class));
		item.setCulture(QueryBase.convertSafe(tuple, columns, UserEntity._culture, String.class));
		item.setLanguage(QueryBase.convertSafe(tuple, columns, UserEntity._language, String.class));
		item.setSubjectId(QueryBase.convertSafe(tuple, columns, UserEntity._subjectId, String.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, UserEntity._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, UserEntity._updatedAt, Instant.class));
		item.setIsActive(QueryBase.convertSafe(tuple, columns, UserEntity._isActive, IsActive.class));
		return item;
	}

}
