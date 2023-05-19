package org.pac4j.gae.client;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.definition.ProfileDefinition;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.gae.credentials.GaeUserCredentials;
import org.pac4j.gae.profile.GaeUserServiceProfile;

import java.util.Optional;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * <p>This class is the OpenID client to authenticate users with UserService on App Engine</p>
 *
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class GaeUserServiceClient extends IndirectClient {

    private static final ProfileDefinition PROFILE_DEFINITION
        = new CommonProfileDefinition(x -> new GaeUserServiceProfile());

    protected UserService service;
    protected String authDomain = null;

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        service = UserServiceFactory.getUserService();
        CommonHelper.assertNotNull("service", this.service);
        setRedirectionActionBuilderIfUndefined(ctx -> {
            val webContext = ctx.webContext();
            val destinationUrl = computeFinalCallbackUrl(webContext);
            val loginUrl = authDomain == null ?  service.createLoginURL(destinationUrl)
                : service.createLoginURL(destinationUrl, authDomain);
            return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, loginUrl));
        });
        setCredentialsExtractorIfUndefined(ctx -> {
            val credentials = new GaeUserCredentials();
            credentials.setUser(service.getCurrentUser());
            return Optional.of(credentials);
        });
        setAuthenticatorIfUndefined((ctx, credentials) -> {
            val user = ((GaeUserCredentials) credentials).getUser();
            if (user != null) {
                UserProfile profile = (GaeUserServiceProfile) PROFILE_DEFINITION.newProfile();
                profile.setId(user.getEmail());
                PROFILE_DEFINITION.convertAndAdd(profile, PROFILE_ATTRIBUTE, CommonProfileDefinition.EMAIL, user.getEmail());
                PROFILE_DEFINITION.convertAndAdd(profile, PROFILE_ATTRIBUTE, CommonProfileDefinition.DISPLAY_NAME, user.getNickname());
                if (service.isUserAdmin()) {
                    profile.addRole(GaeUserServiceProfile.PAC4J_GAE_GLOBAL_ADMIN_ROLE);
                }
                credentials.setUserProfile(profile);
            }
            return Optional.of(credentials);
        });
    }

    /**
     * Set the authDomain for connect to google apps for domain with the UserService
     *
     * @param authDomain the authentication domain
     */
    public void setAuthDomain(final String authDomain) {
        this.authDomain = authDomain;
    }

    /**
     * <p>Getter for the field <code>authDomain</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getAuthDomain() {
        return authDomain;
    }
}
