package org.pac4j.gae.client;

import org.pac4j.core.client.IndirectClientV2;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.gae.credentials.GaeUserCredentials;
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
public class GaeUserServiceClient extends IndirectClientV2<GaeUserCredentials, GaeUserServiceProfile> {

	protected UserService service;
	protected String authDomain = null;

	@Override
    protected void internalInit(final WebContext context) {
	    super.internalInit(context);

		service = UserServiceFactory.getUserService();
		CommonHelper.assertNotNull("service", this.service);
		setProfileDefinition(new CommonProfileDefinition<>(x -> new GaeUserServiceProfile()));
		setRedirectActionBuilder(ctx -> {
			String destinationUrl = computeFinalCallbackUrl(ctx);
			String loginUrl = authDomain == null ?  service.createLoginURL(destinationUrl) : service.createLoginURL(destinationUrl, authDomain);
			return RedirectAction.redirect(loginUrl);
		});
		setCredentialsExtractor(ctx -> {
			GaeUserCredentials credentials = new GaeUserCredentials();
			credentials.setUser(service.getCurrentUser());
			return credentials;
		});
		setAuthenticator((credentials, ctx) -> {
			User user = credentials.getUser();
			if (user != null) {
				final GaeUserServiceProfile profile = getProfileDefinition().newProfile();
				profile.setId(user.getEmail());
				getProfileDefinition().convertAndAdd(profile, CommonProfileDefinition.EMAIL, user.getEmail());
				getProfileDefinition().convertAndAdd(profile, CommonProfileDefinition.DISPLAY_NAME, user.getNickname());
				if (service.isUserAdmin()) {
					profile.addRole(GaeUserServiceProfile.PAC4J_GAE_GLOBAL_ADMIN_ROLE);
				}
				credentials.setUserProfile(profile);
			}
		});
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
