package gr.cite.intelcomp.evaluationworkbench.service.keycloak;

import gr.cite.intelcomp.evaluationworkbench.convention.ConventionService;
import org.jetbrains.annotations.NotNull;

import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    private final MyKeycloakAdminRestApi api;
    private final KeycloakResourcesConfiguration configuration;
    private final ConventionService conventionService;

    @Autowired
    public KeycloakServiceImpl(MyKeycloakAdminRestApi api, KeycloakResourcesConfiguration configuration, ConventionService conventionService) {
        this.api = api;
        this.configuration = configuration;
	    this.conventionService = conventionService;
    }

    @Override
    public void addUserToGroup(@NotNull String subjectId, String groupId) {
	    this.api.users().addUserToGroup(subjectId, groupId);
    }

    @Override
    public void removeUserFromGroup(@NotNull String subjectId, String groupId) {
	    this.api.users().removeUserFromGroup(subjectId, groupId);
    }
    
    @Override
    public List<String> getUserGroups(String subjectId) {
        if (this.configuration.getProperties().getAuthorities() == null) return new ArrayList<>();
        List<GroupRepresentation> group = this.api.users().getGroups(subjectId);
        if (group != null) return group.stream().map(GroupRepresentation::getId).toList();
        return new ArrayList<>();
    }

    @Override
    public void removeFromAllGroups(String subjectId){
        List<String> existingGroups = this.getUserGroups(subjectId);
        for (String existingGroup : existingGroups){
            this.removeUserFromGroup(subjectId, existingGroup);
        }
    }
    
    @Override
    public void addUserToGlobalRoleGroup(String subjectId, String role) {
        if (this.configuration.getProperties().getAuthorities() == null) return;
        KeycloakAuthorityProperties properties = this.configuration.getProperties().getAuthorities().getOrDefault(role, null);
        if (properties != null) this.addUserToGroup(subjectId, properties.getGroupId());
    }

    @Override
    public void removeUserGlobalRoleGroup(@NotNull String subjectId, String role) {
        if (this.configuration.getProperties().getAuthorities() == null) return;
        KeycloakAuthorityProperties properties = this.configuration.getProperties().getAuthorities().getOrDefault(role, null);
        if (properties != null) this.removeUserFromGroup(subjectId, properties.getGroupId());
    }

}