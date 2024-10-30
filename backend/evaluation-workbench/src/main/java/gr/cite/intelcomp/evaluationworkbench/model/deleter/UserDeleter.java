package gr.cite.intelcomp.evaluationworkbench.model.deleter;

import gr.cite.intelcomp.evaluationworkbench.common.enums.IsActive;
import gr.cite.intelcomp.evaluationworkbench.data.UserContactInfoEntity;
import gr.cite.intelcomp.evaluationworkbench.data.UserEntity;
import gr.cite.intelcomp.evaluationworkbench.data.UserRoleEntity;
import gr.cite.intelcomp.evaluationworkbench.query.UserContactInfoQuery;
import gr.cite.intelcomp.evaluationworkbench.query.UserQuery;
import gr.cite.intelcomp.evaluationworkbench.query.UserRoleQuery;
import gr.cite.tools.data.deleter.Deleter;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserDeleter implements Deleter {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserDeleter.class));
    @PersistenceContext
    private EntityManager entityManager;
    protected final QueryFactory queryFactory;

    protected final DeleterFactory deleterFactory;

    @Autowired
    public UserDeleter(
            QueryFactory queryFactory,
            DeleterFactory deleterFactory
    ) {
        this.queryFactory = queryFactory;
        this.deleterFactory = deleterFactory;
    }

    public void deleteAndSaveByIds(List<UUID> ids) throws InvalidApplicationException {
        logger.debug(new MapLogEntry("collecting to delete").And("count", Optional.ofNullable(ids).map(List::size).orElse(0)).And("ids", ids));
        List<UserEntity> data = this.queryFactory.query(UserQuery.class).ids(ids).collect();
        logger.trace("retrieved {} items", Optional.ofNullable(data).map(List::size).orElse(0));
        this.deleteAndSave(data);
    }

    public void deleteAndSave(List<UserEntity> data) throws InvalidApplicationException {
        logger.debug("will delete {} items", Optional.ofNullable(data).map(List::size).orElse(0));
        this.delete(data);
        logger.trace("saving changes");
        this.entityManager.flush();
        logger.trace("changes saved");
    }

    public void delete(List<UserEntity> data) throws InvalidApplicationException {
        logger.debug("will delete {} items", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty())
            return;
        List<UUID> ids = data.stream().map(UserEntity::getId).distinct().collect(Collectors.toList());
        {
            logger.debug("checking related - {}", UserRoleEntity.class.getSimpleName());
            List<UserRoleEntity> items = this.queryFactory.query(UserRoleQuery.class).userIds(ids).collect();
            UserRoleDeleter deleter = this.deleterFactory.deleter(UserRoleDeleter.class);
            deleter.delete(items);
        }
        {
            logger.debug("checking related - {}", UserContactInfoEntity.class.getSimpleName());
            List<UserContactInfoEntity> items = this.queryFactory.query(UserContactInfoQuery.class).userIds(ids).collect();
            UserContactInfoDeleter deleter = this.deleterFactory.deleter(UserContactInfoDeleter.class);
            deleter.delete(items);
        }
        Instant now = Instant.now();

        for (UserEntity item : data) {
            logger.trace("deleting item {}", item.getId());
            item.setIsActive(IsActive.INACTIVE);
            item.setUpdatedAt(now);
            logger.trace("updating item");
            this.entityManager.merge(item);
            logger.trace("updated item");
        }
    }

}
