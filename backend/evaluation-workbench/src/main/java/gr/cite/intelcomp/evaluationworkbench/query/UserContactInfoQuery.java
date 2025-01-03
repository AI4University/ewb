package gr.cite.intelcomp.evaluationworkbench.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.evaluationworkbench.authorization.AuthorizationFlags;
import gr.cite.intelcomp.evaluationworkbench.authorization.Permission;
import gr.cite.intelcomp.evaluationworkbench.common.enums.ContactInfoType;
import gr.cite.intelcomp.evaluationworkbench.common.scope.user.UserScope;
import gr.cite.intelcomp.evaluationworkbench.data.UserContactInfoEntity;
import gr.cite.intelcomp.evaluationworkbench.model.UserContactInfo;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
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
public class UserContactInfoQuery extends QueryBase<UserContactInfoEntity> {
    private Collection<UUID> ids;
    private Collection<UUID> excludedIds;
    private Collection<UUID> excludedUserIds;
    private Collection<UUID> userIds;
    private Collection<String> values;
    private Collection<ContactInfoType> types;
    
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);
    private final UserScope userScope;
    private final AuthorizationService authService;
    public UserContactInfoQuery(UserScope userScope, AuthorizationService authService) {
	    this.userScope = userScope;
        this.authService = authService;
    }

    public UserContactInfoQuery ids(UUID value) {
        this.ids = List.of(value);
        return this;
    }

    public UserContactInfoQuery ids(UUID... value) {
        this.ids = Arrays.asList(value);
        return this;
    }

    public UserContactInfoQuery ids(Collection<UUID> values) {
        this.ids = values;
        return this;
    }
    
    public UserContactInfoQuery excludedIds(Collection<UUID> values) {
        this.excludedIds = values;
        return this;
    }

    public UserContactInfoQuery excludedIds(UUID value) {
        this.excludedIds = List.of(value);
        return this;
    }

    public UserContactInfoQuery excludedIds(UUID... value) {
        this.excludedIds = Arrays.asList(value);
        return this;
    }

    public UserContactInfoQuery excludedUserIds(Collection<UUID> values) {
        this.excludedUserIds = values;
        return this;
    }

    public UserContactInfoQuery excludedUserIds(UUID value) {
        this.excludedUserIds = List.of(value);
        return this;
    }

    public UserContactInfoQuery excludedUserIds(UUID... value) {
        this.excludedUserIds = Arrays.asList(value);
        return this;
    }

    public UserContactInfoQuery userIds(UUID value) {
        this.userIds = List.of(value);
        return this;
    }

    public UserContactInfoQuery userIds(UUID... value) {
        this.userIds = Arrays.asList(value);
        return this;
    }

    public UserContactInfoQuery userIds(Collection<UUID> values) {
        this.userIds = values;
        return this;
    }

    public UserContactInfoQuery values(String value) {
        this.values = List.of(value);
        return this;
    }

    public UserContactInfoQuery values(String... value) {
        this.values = Arrays.asList(value);
        return this;
    }

    public UserContactInfoQuery values(Collection<String> values) {
        this.values = values;
        return this;
    }

    public UserContactInfoQuery types(ContactInfoType value) {
        this.types = List.of(value);
        return this;
    }

    public UserContactInfoQuery types(ContactInfoType... value) {
        this.types = Arrays.asList(value);
        return this;
    }

    public UserContactInfoQuery types(Collection<ContactInfoType> values) {
        this.types = values;
        return this;
    }

    public UserContactInfoQuery authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }


    @Override
    protected Boolean isFalseQuery() {
        return
                this.isEmpty(this.ids) ||
                        this.isEmpty(this.userIds) ||
                        this.isEmpty(this.excludedIds) ||
                        this.isEmpty(this.values) ||
                        this.isEmpty(this.excludedIds);
    }

    @Override
    protected Class<UserContactInfoEntity> entityClass() {
        return UserContactInfoEntity.class;
    }

    @Override
    protected <X, Y> Predicate applyAuthZ(QueryContext<X, Y> queryContext) {
        if (this.authorize.contains(AuthorizationFlags.None)) return null;
        if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseUser)) return null;
        UUID userId;
        if (this.authorize.contains(AuthorizationFlags.Owner)) userId = this.userScope.getUserIdSafe();
        else  userId = null;

        List<Predicate> predicates = new ArrayList<>();
        if (userId != null) {
            predicates.add(queryContext.CriteriaBuilder.or(
                    queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._userId)).value(userId)  //Creates a false query
            ));
        }
        if (!predicates.isEmpty()) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return queryContext.CriteriaBuilder.or(); //Creates a false query
        }
    }
    
    @Override
    protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
        List<Predicate> predicates = new ArrayList<>();
        if (this.ids != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._id));
            for (UUID item : this.ids)
                inClause.value(item);
            predicates.add(inClause);
        }
        if (this.types != null) {
            CriteriaBuilder.In<ContactInfoType> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._type));
            for (ContactInfoType item : this.types)
                inClause.value(item);
            predicates.add(inClause);
        }
        if (this.userIds != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._userId));
            for (UUID item : this.userIds)
                inClause.value(item);
            predicates.add(inClause);
        }
        if (this.excludedIds != null) {
            CriteriaBuilder.In<UUID> notInClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._id));
            for (UUID item : this.excludedIds)
                notInClause.value(item);
            predicates.add(notInClause.not());
        }
        if (this.excludedUserIds != null) {
            CriteriaBuilder.In<UUID> notInClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._userId));
            for (UUID item : this.excludedUserIds)
                notInClause.value(item);
            predicates.add(notInClause.not());
        }
        if (this.values != null) {
            CriteriaBuilder.In<String> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._value));
            for (String item : this.values)
                inClause.value(item);
            predicates.add(inClause);
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
        if (item.match(UserContactInfo._id)) return UserContactInfoEntity._id;
        else if (item.match(UserContactInfo._value)) return UserContactInfoEntity._value;
        else if (item.match(UserContactInfo._ordinal)) return UserContactInfoEntity._ordinal;
        else if (item.prefix(UserContactInfo._user)) return UserContactInfoEntity._userId;
        else if (item.match(UserContactInfo._user)) return UserContactInfoEntity._userId;
        else if (item.match(UserContactInfo._type)) return UserContactInfoEntity._type;
        else if (item.match(UserContactInfo._createdAt) ) return UserContactInfoEntity._createdAt;
        else return null;
    }

    @Override
    protected UserContactInfoEntity convert(Tuple tuple, Set<String> columns) {
        UserContactInfoEntity item = new UserContactInfoEntity();
        item.setId(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._id, UUID.class));
        item.setValue(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._value, String.class));
        item.setType(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._type, ContactInfoType.class));
        item.setOrdinal(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._ordinal, Integer.class));
        item.setUserId(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._userId, UUID.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._createdAt, Instant.class));
        return item;
    }

}
