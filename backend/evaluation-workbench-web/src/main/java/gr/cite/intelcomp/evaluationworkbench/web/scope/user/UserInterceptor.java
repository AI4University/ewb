package gr.cite.intelcomp.evaluationworkbench.web.scope.user;


import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.intelcomp.evaluationworkbench.authorization.AuthorizationConfiguration;
import gr.cite.intelcomp.evaluationworkbench.authorization.ClaimNames;
import gr.cite.intelcomp.evaluationworkbench.common.enums.ContactInfoType;
import gr.cite.intelcomp.evaluationworkbench.common.enums.IsActive;
import gr.cite.intelcomp.evaluationworkbench.common.lock.LockByKeyManager;
import gr.cite.intelcomp.evaluationworkbench.common.scope.user.UserScope;
import gr.cite.intelcomp.evaluationworkbench.convention.ConventionService;
import gr.cite.intelcomp.evaluationworkbench.data.UserContactInfoEntity;
import gr.cite.intelcomp.evaluationworkbench.data.UserEntity;
import gr.cite.intelcomp.evaluationworkbench.data.UserRoleEntity;
import gr.cite.intelcomp.evaluationworkbench.locale.LocaleService;
import gr.cite.intelcomp.evaluationworkbench.model.UserContactInfo;
import gr.cite.intelcomp.evaluationworkbench.query.UserContactInfoQuery;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.logging.LoggerService;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import javax.management.InvalidApplicationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class UserInterceptor implements WebRequestInterceptor {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserInterceptor.class));
	private final UserScope userScope;
	private final ClaimExtractor claimExtractor;
	private final CurrentPrincipalResolver currentPrincipalResolver;
	private final LocaleService localeService;
	private final PlatformTransactionManager transactionManager;
	private final UserInterceptorCacheService userInterceptorCacheService;
	private final AuthorizationConfiguration authorizationConfiguration;
	@PersistenceContext
	public EntityManager entityManager;
	private final QueryFactory queryFactory;
	private final LockByKeyManager lockByKeyManager;
	private final ConventionService conventionService;

	@Autowired
	public UserInterceptor(
            UserScope userScope,
            LocaleService localeService,
            ClaimExtractor claimExtractor,
            CurrentPrincipalResolver currentPrincipalResolver,
            PlatformTransactionManager transactionManager,
            UserInterceptorCacheService userInterceptorCacheService, AuthorizationConfiguration authorizationConfiguration, QueryFactory queryFactory, LockByKeyManager lockByKeyManager, ConventionService conventionService
    ) {
		this.userScope = userScope;
		this.localeService = localeService;
		this.currentPrincipalResolver = currentPrincipalResolver;
		this.claimExtractor = claimExtractor;
		this.transactionManager = transactionManager;
		this.userInterceptorCacheService = userInterceptorCacheService;
        this.authorizationConfiguration = authorizationConfiguration;
        this.queryFactory = queryFactory;
        this.lockByKeyManager = lockByKeyManager;
        this.conventionService = conventionService;
    }

	@Override
	public void preHandle(WebRequest request) throws InterruptedException, InvalidApplicationException {
		UUID userId = null;
		if (this.currentPrincipalResolver.currentPrincipal().isAuthenticated()) {
			String subjectId = this.claimExtractor.subjectString(this.currentPrincipalResolver.currentPrincipal());
			if (subjectId == null || subjectId.isBlank()) throw new MyForbiddenException("Empty subjects not allowed");

			UserInterceptorCacheService.UserInterceptorCacheValue cacheValue = this.userInterceptorCacheService.lookup(this.userInterceptorCacheService.buildKey(subjectId));
			if (cacheValue != null && this.emailExistsToPrincipal(cacheValue.getProviderEmail()) && this.userRolesSynced(cacheValue.getRoles())) {
				userId = cacheValue.getUserId();
			} else {
				boolean usedResource = false;
				usedResource = this.lockByKeyManager.tryLock(subjectId, 5000, TimeUnit.MILLISECONDS);
				String email = this.getEmailFromClaims();

				DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
				definition.setName(UUID.randomUUID().toString());
				definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
				definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
				TransactionStatus status = null;
				try {
					status = this.transactionManager.getTransaction(definition);

					userId = this.getUserIdFromDatabase(subjectId);
					boolean isNewUser = userId == null;
					if (isNewUser) {
						UserEntity user = this.addNewUser(subjectId, email);
						userId = user.getId();
					}
					this.entityManager.flush();

					if (!isNewUser) {
						this.syncUserWithClaims(userId, subjectId);
					}

					this.entityManager.flush();
					this.transactionManager.commit(status);
				} catch (Exception ex) {
					if (status != null) this.transactionManager.rollback(status);
					throw ex;
				}

				cacheValue = new UserInterceptorCacheService.UserInterceptorCacheValue(subjectId, userId);
				cacheValue.setRoles(this.getRolesFromClaims());
				if (email != null && !email.isBlank()) cacheValue.setProviderEmail(email);

				this.userInterceptorCacheService.put(cacheValue);
				if (usedResource) this.lockByKeyManager.unlock(subjectId);
			}
		}
		this.userScope.setUserId(userId);
	}

	private boolean syncUserWithClaims(UUID userId, String subjectId) {
		List<String> existingUserEmails = this.collectUserEmails(userId);
		boolean hasChanges = false;
		if (!this.containsPrincipalEmail(existingUserEmails)) {
			String email = this.getEmailFromClaims();
			long contactUsedByOthersCount = this.queryFactory.query(UserContactInfoQuery.class).excludedUserIds(userId).types(ContactInfoType.Email).values(email).count();
			if (contactUsedByOthersCount > 0) {
				logger.warn("user contact exists to other user" + email);
			} else {
				Long emailContactsCount = this.queryFactory.query(UserContactInfoQuery.class).userIds(userId).types(ContactInfoType.Email).count();
				UserContactInfoEntity contactInfo = this.buildEmailContact(userId, email);
				contactInfo.setOrdinal(emailContactsCount.intValue());
				hasChanges = true;
				this.entityManager.persist(contactInfo);
			}
		}

		List<String> existingUserRoles = this.collectUserRoles(userId);
		if (!this.userRolesSynced(existingUserRoles)) {
			this.syncRoles(userId);
			hasChanges = true;
		}

		return hasChanges;
	}

	private UUID getUserIdFromDatabase(String subjectId) throws InvalidApplicationException {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = criteriaBuilder.createQuery(Tuple.class);
		Root<UserEntity> root = query.from(UserEntity.class);
		query.where(
				criteriaBuilder.and(
						criteriaBuilder.equal(root.get(UserEntity._subjectId), subjectId),
						criteriaBuilder.equal(root.get(UserEntity._isActive), IsActive.ACTIVE)
				));

		query.multiselect(root.get(UserEntity._id).alias(UserEntity._id));

		List<Tuple> results = this.entityManager.createQuery(query).getResultList();
		if (results.size() == 1) {
			Object o;
			try {
				o = results.get(0).get(UserEntity._id);
			} catch (IllegalArgumentException e) {
				return null;
			}
			if (o == null) return null;
			try {
				return UUID.class.cast(o);
			} catch (ClassCastException e) {
				return null;
			}
		}
		return null;
	}

	private List<String> getRolesFromClaims() {
		List<String> claimsRoles = this.claimExtractor.asStrings(this.currentPrincipalResolver.currentPrincipal(), ClaimNames.GlobalRolesClaimName);
		if (claimsRoles == null) claimsRoles = new ArrayList<>();
		claimsRoles = claimsRoles.stream().filter(x -> x != null && !x.isBlank() && (this.conventionService.isListNullOrEmpty(this.authorizationConfiguration.getAuthorizationProperties().getAllowedGlobalRoles()) || this.authorizationConfiguration.getAuthorizationProperties().getAllowedGlobalRoles().contains(x))).distinct().toList();
		claimsRoles = claimsRoles.stream().filter(x -> x != null && !x.isBlank()).distinct().toList();
		return claimsRoles;
	}

	private void syncRoles(UUID userId) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<UserRoleEntity> query = criteriaBuilder.createQuery(UserRoleEntity.class);
		Root<UserRoleEntity> root = query.from(UserRoleEntity.class);

		CriteriaBuilder.In<String> inRolesClause = criteriaBuilder.in(root.get(UserRoleEntity._role));
		for (String item : this.authorizationConfiguration.getAuthorizationProperties().getAllowedGlobalRoles()) inRolesClause.value(item);
		query.where(criteriaBuilder.and(
				criteriaBuilder.equal(root.get(UserRoleEntity._userId), userId),
				this.conventionService.isListNullOrEmpty(this.authorizationConfiguration.getAuthorizationProperties().getAllowedGlobalRoles()) ? criteriaBuilder.isNotNull(root.get(UserRoleEntity._role))  : inRolesClause
		));
		List<UserRoleEntity> existingUserRoles = this.entityManager.createQuery(query).getResultList();

		List<UUID> foundRoles = new ArrayList<>();
		for (String claimRole : this.getRolesFromClaims()) {
			UserRoleEntity roleEntity = existingUserRoles.stream().filter(x -> x.getRole().equals(claimRole)).findFirst().orElse(null);
			if (roleEntity == null) {
				roleEntity = this.buildRole(userId, claimRole);
				this.entityManager.persist(roleEntity);
			}
			foundRoles.add(roleEntity.getId());
		}
		for (UserRoleEntity existing : existingUserRoles) {
			if (!foundRoles.contains(existing.getId())) {
				this.entityManager.remove(existing);
			}
		}
	}

	private List<String> collectUserRoles(UUID userId) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<UserRoleEntity> query = criteriaBuilder.createQuery(UserRoleEntity.class);
		Root<UserRoleEntity> root = query.from(UserRoleEntity.class);

		CriteriaBuilder.In<String> inRolesClause = criteriaBuilder.in(root.get(UserRoleEntity._role));
		for (String item : this.authorizationConfiguration.getAuthorizationProperties().getAllowedGlobalRoles()) inRolesClause.value(item);

		query.where(criteriaBuilder.and(
				criteriaBuilder.equal(root.get(UserRoleEntity._userId), userId),
				this.conventionService.isListNullOrEmpty(this.authorizationConfiguration.getAuthorizationProperties().getAllowedGlobalRoles()) ? criteriaBuilder.isNotNull(root.get(UserRoleEntity._role))  : inRolesClause
		)).multiselect(root.get(UserRoleEntity._role).alias(UserRoleEntity._role));
		List<UserRoleEntity> results = this.entityManager.createQuery(query).getResultList();

		return results.stream().map(UserRoleEntity::getRole).toList();
	}

	private List<String> collectUserEmails(UUID userId) {
		List<UserContactInfoEntity> items = this.queryFactory.query(UserContactInfoQuery.class).userIds(userId).types(ContactInfoType.Email).collectAs(new BaseFieldSet().ensure(UserContactInfo._value));
		return items == null ? new ArrayList<>() : items.stream().map(UserContactInfoEntity::getValue).toList();
	}

	private boolean containsPrincipalEmail(List<String> existingUserEmails) {
		String email = this.getEmailFromClaims();
		return email == null || email.isBlank() ||
				(existingUserEmails != null && existingUserEmails.stream().anyMatch(email::equals));
	}

	private boolean emailExistsToPrincipal(String existingUserEmail) {
		String email = this.getEmailFromClaims();
		return email == null || email.isBlank() || email.equalsIgnoreCase(existingUserEmail);
	}


	private boolean userRolesSynced(List<String> existingUserRoles) {
		List<String> claimsRoles = this.getRolesFromClaims();
		if (existingUserRoles == null) existingUserRoles = new ArrayList<>();
		existingUserRoles = existingUserRoles.stream().filter(x -> x != null && !x.isBlank()).distinct().toList();
		if (claimsRoles.size() != existingUserRoles.size()) return false;

		for (String claim : claimsRoles) {
			if (existingUserRoles.stream().noneMatch(claim::equalsIgnoreCase)) return false;
		}
		return true;
	}

	private String getEmailFromClaims() {
		String email = this.claimExtractor.email(this.currentPrincipalResolver.currentPrincipal());
		if (email == null || email.isBlank() || !EmailValidator.getInstance().isValid(email)) return null;
		return email.trim();
	}

	private String getProviderFromClaims() {
		String provider = this.claimExtractor.asString(this.currentPrincipalResolver.currentPrincipal(), ClaimNames.ExternalProviderName);
		if (provider == null || provider.isBlank()) return null;
		return provider.trim();
	}

	private UserRoleEntity buildRole(UUID userId, String role) {
		UserRoleEntity data = new UserRoleEntity();
		data.setId(UUID.randomUUID());
		data.setUserId(userId);
		data.setRole(role);
		data.setCreatedAt(Instant.now());
		return data;
	}

	private UserContactInfoEntity buildEmailContact(UUID userId, String email) {
		UserContactInfoEntity data = new UserContactInfoEntity();
		data.setId(UUID.randomUUID());
		data.setUserId(userId);
		data.setValue(email);
		data.setType(ContactInfoType.Email);
		data.setOrdinal(0);
		data.setCreatedAt(Instant.now());
		return data;
	}

	private UserEntity addNewUser(String subjectId, String email) {
		String name = this.claimExtractor.name(this.currentPrincipalResolver.currentPrincipal());
		String familyName = this.claimExtractor.familyName(this.currentPrincipalResolver.currentPrincipal());
		if (name == null) name = subjectId;
		UserEntity user = new UserEntity();
		user.setId(UUID.randomUUID());
		user.setCreatedAt(Instant.now());
		user.setUpdatedAt(Instant.now());
		user.setFirstName(name);
		user.setLastName(familyName == null ? name : familyName);
		user.setIsActive(IsActive.ACTIVE);
		user.setSubjectId(subjectId);
		user.setCulture(this.localeService.cultureName());
		user.setTimezone(this.localeService.timezoneName());
		user.setLanguage(this.localeService.language());

		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setName(UUID.randomUUID().toString());
		definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus status = null;
		try {
			status = transactionManager.getTransaction(definition);
			this.entityManager.persist(user);

			this.entityManager.flush();
			transactionManager.commit(status);
		} catch (Exception ex) {
			if (status != null) transactionManager.rollback(status);
			throw ex;
		}

		List<String> roles = this.getRolesFromClaims();
		if (email != null && !email.isBlank()) {
			UserContactInfoEntity contactInfo = this.buildEmailContact(user.getId(), email);
			this.entityManager.persist(contactInfo);
		}
		if (roles != null) {
			for (String role : roles) {
				UserRoleEntity roleEntity = this.buildRole(user.getId(), role);
				this.entityManager.persist(roleEntity);
			}
		}

		return user;
	}

	@Override
	public void postHandle(@NonNull WebRequest request, ModelMap model) {
		this.userScope.setUserId(null);
	}

	@Override
	public void afterCompletion(@NonNull WebRequest request, Exception ex) {
	}
}
