package gr.cite.intelcomp.evaluationworkbench.service.user;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.evaluationworkbench.authorization.AuthorizationConfiguration;
import gr.cite.intelcomp.evaluationworkbench.authorization.AuthorizationFlags;
import gr.cite.intelcomp.evaluationworkbench.authorization.Permission;
import gr.cite.intelcomp.evaluationworkbench.convention.ConventionService;
import gr.cite.intelcomp.evaluationworkbench.data.UserEntity;
import gr.cite.intelcomp.evaluationworkbench.data.UserRoleEntity;
import gr.cite.intelcomp.evaluationworkbench.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.evaluationworkbench.model.User;
import gr.cite.intelcomp.evaluationworkbench.model.builder.UserBuilder;
import gr.cite.intelcomp.evaluationworkbench.model.deleter.UserDeleter;
import gr.cite.intelcomp.evaluationworkbench.model.deleter.UserRoleDeleter;
import gr.cite.intelcomp.evaluationworkbench.model.persist.UserRolePatchPersist;
import gr.cite.intelcomp.evaluationworkbench.query.UserQuery;
import gr.cite.intelcomp.evaluationworkbench.query.UserRoleQuery;
import gr.cite.intelcomp.evaluationworkbench.service.keycloak.KeycloakService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import gr.cite.tools.validation.ValidatorFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserServiceImpl.class));

    @PersistenceContext
    private EntityManager entityManager;

    private final AuthorizationService authorizationService;

    private final DeleterFactory deleterFactory;

    private final BuilderFactory builderFactory;

    private final ConventionService conventionService;

    private final ErrorThesaurusProperties errors;

    private final MessageSource messageSource;
    private final QueryFactory queryFactory;
    private final KeycloakService keycloakService;
    private final ValidatorFactory validatorFactory;
    private final AuthorizationConfiguration authorizationConfiguration;
    @Autowired
    public UserServiceImpl(
            AuthorizationService authorizationService,
            DeleterFactory deleterFactory,
            BuilderFactory builderFactory,
            ConventionService conventionService,
            ErrorThesaurusProperties errors,
            MessageSource messageSource,
            QueryFactory queryFactory,
            KeycloakService keycloakService,
            ValidatorFactory validatorFactory,
            AuthorizationConfiguration authorizationConfiguration) {
        this.authorizationService = authorizationService;
        this.deleterFactory = deleterFactory;
        this.builderFactory = builderFactory;
        this.conventionService = conventionService;
        this.errors = errors;
        this.messageSource = messageSource;
        this.queryFactory = queryFactory;
        this.keycloakService = keycloakService;
        this.validatorFactory = validatorFactory;
        this.authorizationConfiguration = authorizationConfiguration;
    }

    //region delete

    @Override
    public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug("deleting User: {}", id);

        this.authorizationService.authorizeForce(Permission.DeleteUser);
        this.deleterFactory.deleter(UserDeleter.class).deleteAndSaveByIds(List.of(id));
    }
    
    //endregion
    
    @Override
    public User patchRoles(UserRolePatchPersist model, FieldSet fields) throws InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting data UserRole").And("model", model).And("fields", fields));

        this.authorizationService.authorizeForce(Permission.EditUser);
        boolean foundGlobalRole = false;
        for (String role: model.getRoles()) {
            if (authorizationConfiguration.getAuthorizationProperties().getAllowedGlobalRoles().contains(role)) foundGlobalRole = true;
        }
        if (!foundGlobalRole) throw new MyApplicationException("role not found");

        UserEntity data = this.entityManager.find(UserEntity.class, model.getId());
        if (data == null) throw new MyNotFoundException(this.messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), User.class.getSimpleName()}, LocaleContextHolder.getLocale()));
        if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());

        this.applyGlobalRoles(data.getId(), model);

        this.entityManager.flush();

        this.syncKeycloakRoles(data.getId());
        
        return this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(BaseFieldSet.build(fields, User._id), data);
    }
    
    private void applyGlobalRoles(UUID userId, UserRolePatchPersist model) throws InvalidApplicationException {

        List<UserRoleEntity> existingItems = this.queryFactory.query(UserRoleQuery.class).userIds(userId).tenantIsSet(false).roles(this.authorizationConfiguration.getAuthorizationProperties().getAllowedGlobalRoles()).collect();
        List<UUID> foundIds = new ArrayList<>();
        for (String roleName : model.getRoles().stream().filter(x -> x != null && !x.isBlank() && this.authorizationConfiguration.getAuthorizationProperties().getAllowedGlobalRoles().contains(x)).distinct().toList()) {
            UserRoleEntity item = existingItems.stream().filter(x -> x.getRole().equals(roleName)).findFirst().orElse(null);
            if (item == null) {
                item = new UserRoleEntity();
                item.setId(UUID.randomUUID());
                item.setUserId(userId);
                item.setRole(roleName);
                item.setCreatedAt(Instant.now());
                this.entityManager.persist(item);
            }
            foundIds.add(item.getId());
        }

        this.entityManager.flush();

        List<UserRoleEntity> toDelete = existingItems.stream().filter(x -> foundIds.stream().noneMatch(y -> y.equals(x.getId()))).collect(Collectors.toList());
        this.deleterFactory.deleter(UserRoleDeleter.class).deleteAndSave(toDelete);

        this.entityManager.flush();

    }

    private void syncKeycloakRoles(UUID userId) throws InvalidApplicationException {
        UserEntity user = this.queryFactory.query(UserQuery.class).disableTracking().ids(userId).first();
        if (user == null) throw new MyNotFoundException(this.messageSource.getMessage("General_ItemNotFound", new Object[]{userId, User.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        List<UserRoleEntity> userRoles = this.queryFactory.query(UserRoleQuery.class).disableTracking().userIds(userId).collect();
        if (this.conventionService.isListNullOrEmpty(userRoles)) throw new MyApplicationException("roles not found");

        this.keycloakService.removeFromAllGroups(user.getSubjectId());
        for (UserRoleEntity userRole : userRoles) {
            if (this.authorizationConfiguration.getAuthorizationProperties().getAllowedGlobalRoles().contains(userRole.getRole())){
                this.keycloakService.addUserToGlobalRoleGroup(user.getSubjectId(), userRole.getRole());
            }
        }
    }

}