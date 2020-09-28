package org.pac4j.gae.client;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.exception.http.RedirectionActionHelper;
import org.pac4j.core.profile.definition.ProfileDefinition;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.gae.credentials.GaeUserCredentials;
import org.pac4j.gae.profile.GaeUserServiceProfile;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.util.Optional;

/**
 * <p>This class is the OpenID client to authenticate users with UserService on App Engine</p>
 *
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class GaeUserServiceClient extends IndirectClient {

    private static final ProfileDefinition<GaeUserServiceProfile> PROFILE_DEFINITION
        = new CommonProfileDefinition<>(x -> new GaeUserServiceProfile());

    protected UserService service;
    protected String authDomain = null;

    @Override
    protected void clientInit() {
        service = UserServiceFactory.getUserService();
        CommonHelper.assertNotNull("service", this.service);
        defaultRedirectionActionBuilder(ctx -> {
            final String destinationUrl = computeFinalCallbackUrl(ctx);
            final String loginUrl = authDomain == null ?  service.createLoginURL(destinationUrl)
                : service.createLoginURL(destinationUrl, authDomain);
            return Optional.of(RedirectionActionHelper.buildRedirectUrlAction(ctx, loginUrl));
        });
        defaultCredentialsExtractor(ctx -> {
            final GaeUserCredentials credentials = new GaeUserCredentials();
            credentials.setUser(service.getCurrentUser());
            return Optional.of(credentials);
        });
        defaultAuthenticator((credentials, ctx) -> {
            final User user = ((GaeUserCredentials) credentials).getUser();
            if (user != null) {
                final GaeUserServiceProfile profile = PROFILE_DEFINITION.newProfile();
                profile.setId(user.getEmail());
                PROFILE_DEFINITION.convertAndAdd(profile, PROFILE_ATTRIBUTE, CommonProfileDefinition.EMAIL, user.getEmail());
                PROFILE_DEFINITION.convertAndAdd(profile, PROFILE_ATTRIBUTE, CommonProfileDefinition.DISPLAY_NAME, user.getNickname());
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
