package gr.cite.intelcomp.evaluationworkbench.query.lookup;

import gr.cite.intelcomp.evaluationworkbench.common.enums.IsActive;
import gr.cite.intelcomp.evaluationworkbench.query.UserQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;


import java.util.List;
import java.util.UUID;

public class UserLookup extends Lookup {
	private String like;
	private List<IsActive> isActive;
	private List<UUID> ids;
	private List<UUID> excludedIds;
	private UserRoleLookup userRoleSubQuery;

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public List<IsActive> getIsActive() {
		return isActive;
	}

	public void setIsActive(List<IsActive> isActive) {
		this.isActive = isActive;
	}

	public List<UUID> getIds() {
		return ids;
	}

	public void setIds(List<UUID> ids) {
		this.ids = ids;
	}

	public List<UUID> getExcludedIds() {
		return excludedIds;
	}

	public void setExcludedIds(List<UUID> excludedIds) {
		this.excludedIds = excludedIds;
	}

	public UserRoleLookup getUserRoleSubQuery() {
		return userRoleSubQuery;
	}

	public void setUserRoleSubQuery(UserRoleLookup userRoleSubQuery) {
		this.userRoleSubQuery = userRoleSubQuery;
	}

	public UserQuery enrich(QueryFactory queryFactory) {
		UserQuery query = queryFactory.query(UserQuery.class);
		if (this.like != null) query.like(this.like);
		if (this.isActive != null) query.isActive(this.isActive);
		if (this.ids != null) query.ids(this.ids);
		if (this.excludedIds != null) query.excludedIds(this.excludedIds);
		if (this.userRoleSubQuery != null) query.userRoleSubQuery(this.userRoleSubQuery.enrich(queryFactory));
		this.enrichCommon(query);

		return query;
	}


}
