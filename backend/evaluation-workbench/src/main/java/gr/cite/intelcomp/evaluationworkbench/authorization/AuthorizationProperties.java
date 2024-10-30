package gr.cite.intelcomp.evaluationworkbench.authorization;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "authorization")
public class AuthorizationProperties {

	private List<String> globalAdminRoles;;
	private List<String> allowedTenantRoles;
	private List<String> allowedGlobalRoles;

	public List<String> getGlobalAdminRoles() {
		return this.globalAdminRoles;
	}

	public void setGlobalAdminRoles(List<String> globalAdminRoles) {
		this.globalAdminRoles = globalAdminRoles;
	}

	public List<String> getAllowedTenantRoles() {
		return this.allowedTenantRoles;
	}

	public void setAllowedTenantRoles(List<String> allowedTenantRoles) {
		this.allowedTenantRoles = allowedTenantRoles;
	}

	public List<String> getAllowedGlobalRoles() {
		return this.allowedGlobalRoles;
	}

	public void setAllowedGlobalRoles(List<String> allowedGlobalRoles) {
		this.allowedGlobalRoles = allowedGlobalRoles;
	}
}
