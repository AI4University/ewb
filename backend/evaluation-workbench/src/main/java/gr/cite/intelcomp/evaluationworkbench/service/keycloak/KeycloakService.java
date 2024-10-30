package gr.cite.intelcomp.evaluationworkbench.service.keycloak;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface KeycloakService {

    void addUserToGroup(@NotNull String subjectId, String groupId);

    void removeUserFromGroup(@NotNull String subjectId, String groupId);

    List<String> getUserGroups(String subjectId);

    void removeFromAllGroups(String subjectId);

    void addUserToGlobalRoleGroup(String subjectId, String role);
    void removeUserGlobalRoleGroup(@NotNull String subjectId, String role);
}
