package gr.cite.intelcomp.evaluationworkbench.model.builder;

import gr.cite.intelcomp.evaluationworkbench.authorization.AuthorizationConfiguration;
import gr.cite.intelcomp.evaluationworkbench.authorization.AuthorizationFlags;
import gr.cite.intelcomp.evaluationworkbench.common.JsonHandlingService;
import gr.cite.intelcomp.evaluationworkbench.convention.ConventionService;
import gr.cite.intelcomp.evaluationworkbench.data.UserEntity;
import gr.cite.intelcomp.evaluationworkbench.model.User;
import gr.cite.intelcomp.evaluationworkbench.model.UserContactInfo;
import gr.cite.intelcomp.evaluationworkbench.model.UserRole;
import gr.cite.intelcomp.evaluationworkbench.query.UserContactInfoQuery;
import gr.cite.intelcomp.evaluationworkbench.query.UserRoleQuery;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserBuilder extends BaseBuilder<User, UserEntity> {

	private final BuilderFactory builderFactory;
	private final QueryFactory queryFactory;
	private final JsonHandlingService jsonHandlingService;
	private final AuthorizationConfiguration authorizationConfiguration;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public UserBuilder(
            ConventionService conventionService,
            BuilderFactory builderFactory,
            QueryFactory queryFactory, JsonHandlingService jsonHandlingService, AuthorizationConfiguration authorizationConfiguration
    ) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(UserBuilder.class)));
		this.builderFactory = builderFactory;
		this.queryFactory = queryFactory;
        this.jsonHandlingService = jsonHandlingService;
        this.authorizationConfiguration = authorizationConfiguration;
    }

	public UserBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<User> build(FieldSet fields, List<UserEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if(fields == null || datas == null || fields.isEmpty()) return new ArrayList<>();


		FieldSet usersContactInfoFields = fields.extractPrefixed(this.asPrefix(User._contacts));
		Map<UUID, List<UserContactInfo>> usersContactInfo = this.collectContactInfoList(usersContactInfoFields, datas);

		FieldSet globalRolesFields = fields.extractPrefixed(this.asPrefix(User._globalRoles));
		Map<UUID, List<UserRole>> globalRolesMap = this.collectUserGlobalRoles(globalRolesFields, datas);

		List<User> models = new ArrayList<>();

		for (UserEntity d : datas) {
			User m = new User();
			if (fields.hasField(this.asIndexer(User._id))) m.setId(d.getId());
			if (fields.hasField(this.asIndexer(User._hash))) m.setHash(this.hashValue(d.getUpdatedAt()));
			if (fields.hasField(this.asIndexer(User._firstName))) m.setFirstName(d.getFirstName());
			if (fields.hasField(this.asIndexer(User._lastName))) m.setLastName(d.getLastName());
			if (fields.hasField(this.asIndexer(User._culture))) m.setCulture(d.getCulture());
			if (fields.hasField(this.asIndexer(User._language))) m.setLanguage(d.getLanguage());
			if (fields.hasField(this.asIndexer(User._subjectId))) m.setSubjectId(d.getSubjectId());
			if (fields.hasField(this.asIndexer(User._timezone))) m.setTimezone(d.getTimezone());
			if (fields.hasField(this.asIndexer(User._createdAt))) m.setCreatedAt(d.getCreatedAt());
			if (fields.hasField(this.asIndexer(User._updatedAt))) m.setUpdatedAt(d.getUpdatedAt());
			if (fields.hasField(this.asIndexer(User._isActive))) m.setIsActive(d.getIsActive());
			if (!globalRolesFields.isEmpty() && globalRolesMap != null && !globalRolesFields.isEmpty() && globalRolesMap.containsKey(d.getId())) m.setGlobalRoles(globalRolesMap.get(d.getId()));
			if (!usersContactInfoFields.isEmpty() && usersContactInfo != null && usersContactInfo.containsKey(d.getId())) m.setContacts(usersContactInfo.get(d.getId()));
			models.add(m);
		}
		this.logger.debug("build {} items",Optional.of(models).map(List::size).orElse(0));
		return models;
	}


	private Map<UUID, List<UserContactInfo>> collectContactInfoList(FieldSet fields, List<UserEntity> datas) throws MyApplicationException {
		if (fields.isEmpty() || datas.isEmpty()) return null;
		this.logger.debug("checking related - {}", UserContactInfo.class.getSimpleName());

		Map<UUID, List<UserContactInfo>> itemMap = null;
		FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(this.asIndexer(UserContactInfo._user, User._id));
		UserContactInfoQuery query = this.queryFactory.query(UserContactInfoQuery.class).authorize(this.authorize).userIds(datas.stream().map(UserEntity::getId).distinct().collect(Collectors.toList()));
		itemMap = this.builderFactory.builder(UserContactInfoBuilder.class).authorize(this.authorize).asMasterKey(query, clone, x -> x.getUser().getId());

		if (!fields.hasField(this.asIndexer(UserContactInfo._user, User._id))) {
			itemMap.values().stream().flatMap(List::stream).filter(x -> x != null && x.getUser() != null).map(x -> {
				x.getUser().setId(null);
				return x;
			}).collect(Collectors.toList());
		}
		return itemMap;
	}

	private Map<UUID, List<UserRole>> collectUserGlobalRoles(FieldSet fields, List<UserEntity> data) throws MyApplicationException {
		if (fields.isEmpty() || data.isEmpty()) return null;
		this.logger.debug("checking related - {}", UserRole.class.getSimpleName());

		Map<UUID, List<UserRole>> itemMap;
		FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(this.asIndexer(UserRole._user, User._id));
		UserRoleQuery query = this.queryFactory.query(UserRoleQuery.class).disableTracking().authorize(this.authorize).tenantIsSet(false).roles(this.authorizationConfiguration.getAuthorizationProperties().getAllowedGlobalRoles()).userIds(data.stream().map(UserEntity::getId).distinct().collect(Collectors.toList()));
		itemMap = this.builderFactory.builder(UserRoleBuilder.class).authorize(this.authorize).asMasterKey(query, clone, x -> x.getUser().getId());

		if (!fields.hasField(this.asIndexer(UserRole._user, User._id))) {
			itemMap.values().stream().flatMap(List::stream).filter(x -> x != null && x.getUser() != null).forEach(x -> {
				x.getUser().setId(null);
			});
		}

		return itemMap;
	}
}
