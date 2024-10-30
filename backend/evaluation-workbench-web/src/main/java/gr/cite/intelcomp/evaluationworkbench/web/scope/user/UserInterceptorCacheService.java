package gr.cite.intelcomp.evaluationworkbench.web.scope.user;

import gr.cite.intelcomp.evaluationworkbench.convention.ConventionService;
import gr.cite.intelcomp.evaluationworkbench.event.UserTouchedEvent;
import gr.cite.tools.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class UserInterceptorCacheService extends CacheService<UserInterceptorCacheService.UserInterceptorCacheValue> {

	public static class UserInterceptorCacheValue {

		public UserInterceptorCacheValue() {
		}

		public UserInterceptorCacheValue(String subjectId, UUID userId) {
			this.subjectId = subjectId;
			this.userId = userId;
		}

		private String subjectId;
		private UUID userId;
		private List<String> roles;
		private String providerEmail;

		public String getSubjectId() {
			return subjectId;
		}

		public void setSubjectId(String subjectId) {
			this.subjectId = subjectId;
		}

		public UUID getUserId() {
			return userId;
		}

		public void setUserId(UUID userId) {
			this.userId = userId;
		}

		public List<String> getRoles() {
			return roles;
		}

		public void setRoles(List<String> roles) {
			this.roles = roles;
		}

		public String getProviderEmail() {
			return providerEmail;
		}

		public void setProviderEmail(String providerEmail) {
			this.providerEmail = providerEmail;
		}

	}

	private final ConventionService conventionService;

	@Autowired
	public UserInterceptorCacheService(UserInterceptorCacheOptions options, ConventionService conventionService) {
		super(options);
		this.conventionService = conventionService;
	}

	@EventListener
	public void handleUserTouchedEvent(UserTouchedEvent event) {
		if (!this.conventionService.isNullOrEmpty(event.getSubjectId())) this.evict(this.buildKey(event.getSubjectId()));
		if (!this.conventionService.isNullOrEmpty(event.getPreviousSubjectId())) this.evict(this.buildKey(event.getPreviousSubjectId()));
	}

	@Override
	protected Class<UserInterceptorCacheValue> valueClass() {
		return UserInterceptorCacheValue.class;
	}

	@Override
	public String keyOf(UserInterceptorCacheValue value) {
		return this.buildKey(value.getSubjectId());
	}


	public String buildKey(String subject) {
		return this.generateKey(new HashMap<>() {{
			put("$subject$", subject);
		}});
	}
}
