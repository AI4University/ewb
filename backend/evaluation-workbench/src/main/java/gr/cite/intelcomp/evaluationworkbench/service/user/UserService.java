package gr.cite.intelcomp.evaluationworkbench.service.user;

import gr.cite.intelcomp.evaluationworkbench.model.User;
import gr.cite.intelcomp.evaluationworkbench.model.persist.UserRolePatchPersist;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.UUID;

public interface UserService {

    User patchRoles(UserRolePatchPersist model, FieldSet fields) throws InvalidApplicationException;
    void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;

}
