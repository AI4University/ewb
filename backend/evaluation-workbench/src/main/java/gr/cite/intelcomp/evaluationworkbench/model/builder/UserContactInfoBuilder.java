package gr.cite.intelcomp.evaluationworkbench.model.builder;

import gr.cite.intelcomp.evaluationworkbench.authorization.AuthorizationFlags;
import gr.cite.intelcomp.evaluationworkbench.convention.ConventionService;
import gr.cite.intelcomp.evaluationworkbench.data.UserContactInfoEntity;
import gr.cite.intelcomp.evaluationworkbench.model.User;
import gr.cite.intelcomp.evaluationworkbench.model.UserContactInfo;
import gr.cite.intelcomp.evaluationworkbench.query.UserQuery;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContactInfoBuilder extends BaseBuilder<UserContactInfo, UserContactInfoEntity> {

	private final BuilderFactory builderFactory;
	private final QueryFactory queryFactory;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public UserContactInfoBuilder(ConventionService conventionService, BuilderFactory builderFactory, QueryFactory queryFactory) {
		super(conventionService,  new LoggerService(LoggerFactory.getLogger(UserContactInfoBuilder.class)));
		this.builderFactory = builderFactory;
		this.queryFactory = queryFactory;
	}

	public UserContactInfoBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<UserContactInfo> build(FieldSet fields, List<UserContactInfoEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

		FieldSet userFields = fields.extractPrefixed(this.asPrefix(UserContactInfo._user));
		Map<UUID, User> userMap = this.collectUsers(userFields, data);

		List<UserContactInfo> models = new ArrayList<>();

		for (UserContactInfoEntity d : data) {
			UserContactInfo m = new UserContactInfo();
			if (fields.hasField(this.asIndexer(UserContactInfo._id))) m.setId(d.getId());
			if (fields.hasField(this.asIndexer(UserContactInfo._createdAt))) m.setCreatedAt(d.getCreatedAt());
			if (fields.hasField(this.asIndexer(UserContactInfo._ordinal))) m.setOrdinal(d.getOrdinal());
			if (fields.hasField(this.asIndexer(UserContactInfo._value))) m.setValue(d.getValue());
			if (fields.hasField(this.asIndexer(UserContactInfo._type))) m.setType(d.getType());
			if (!userFields.isEmpty() && userMap != null && userMap.containsKey(d.getUserId())) m.setUser(userMap.get(d.getUserId()));
			models.add(m);
		}
		this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
		return models;
	}

	private Map<UUID, User> collectUsers(FieldSet fields, List<UserContactInfoEntity> data) throws MyApplicationException {
		if (fields.isEmpty() || data.isEmpty()) return null;
		this.logger.debug("checking related - {}", User.class.getSimpleName());

		Map<UUID, User> itemMap;
		if (!fields.hasOtherField(this.asIndexer(User._id))) {
			itemMap = this.asEmpty(
					data.stream().map(UserContactInfoEntity::getUserId).distinct().collect(Collectors.toList()),
					x -> {
						User item = new User();
						item.setId(x);
						return item;
					},
					User::getId);
		} else {
			FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(User._id);
			UserQuery q = this.queryFactory.query(UserQuery.class).disableTracking().authorize(this.authorize).ids(data.stream().map(UserContactInfoEntity::getUserId).distinct().collect(Collectors.toList()));
			itemMap = this.builderFactory.builder(UserBuilder.class).authorize(this.authorize).asForeignKey(q, clone, User::getId);
		}
		if (!fields.hasField(User._id)) {
			itemMap = itemMap.values().stream().filter(Objects::nonNull).peek(x -> x.setId(null)).collect(Collectors.toMap(User::getId, Function.identity()));
		}

		return itemMap;
	}

}
