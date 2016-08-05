package org.pac4j.gae.client;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.gae.credentials.GaeUserCredentials;
import org.pac4j.gae.profile.GaeUserServiceAttributesDefinition;
import org.pac4j.gae.profile.GaeUserServiceProfile;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * <p>This class is the OpenID client to authenticate users with UserService on App Engine</p>
 * 
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class GaeUserServiceClient extends IndirectClient<GaeUserCredentials, GaeUserServiceProfile> {

	protected UserService service;
	protected String authDomain = null;

	@Override
	protected void internalInit(final WebContext context) {
		service = UserServiceFactory.getUserService();
		CommonHelper.assertNotNull("service", this.service);
		CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
	}

	@Override
	protected RedirectAction retrieveRedirectAction(WebContext context) {
		String destinationUrl = computeFinalCallbackUrl(context);
		String loginUrl = authDomain == null ?  service.createLoginURL(destinationUrl) : service.createLoginURL(destinationUrl, authDomain);
		return RedirectAction.redirect(loginUrl);
	}

	@Override
	protected GaeUserCredentials retrieveCredentials(WebContext context)
			throws HttpAction {
		GaeUserCredentials credentials = new GaeUserCredentials();
		credentials.setUser(service.getCurrentUser());
		return credentials;
	}

	@Override
	protected GaeUserServiceProfile retrieveUserProfile(GaeUserCredentials credentials, WebContext context) throws HttpAction {
		User user = credentials.getUser();
		if (user != null) {
			GaeUserServiceProfile gaeUserProfile = new GaeUserServiceProfile();
			gaeUserProfile.setId(user.getEmail());
			gaeUserProfile.addAttribute(GaeUserServiceAttributesDefinition.EMAIL, user.getEmail());
			gaeUserProfile.addAttribute(GaeUserServiceAttributesDefinition.DISPLAYNAME, user.getNickname());
			if (service.isUserAdmin()) {
				gaeUserProfile.addRole(GaeUserServiceProfile.PAC4J_GAE_GLOBAL_ADMIN_ROLE);
			}
			return gaeUserProfile;
		}
		return null;
	}

	/**
	 * Set the authDomain for connect to google apps for domain with the UserService
	 * @param authDomain the authentication domain
	 */
	public void setAuthDomain(final String authDomain) {
		this.authDomain = authDomain;
	}
	
	public String getAuthDomain() {
		return authDomain;
	}
}
