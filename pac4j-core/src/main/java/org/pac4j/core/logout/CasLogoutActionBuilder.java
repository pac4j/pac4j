package org.pac4j.core.logout;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.HttpActionHelper;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * CAS logout action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@ToString
@Slf4j
public class CasLogoutActionBuilder implements LogoutActionBuilder {

    private final String serverLogoutUrl;

    private final String postLogoutUrlParameter;

    public CasLogoutActionBuilder(final String serverLogoutUrl, final String postLogoutUrlParameter) {
        if (isNotBlank(serverLogoutUrl)) {
            assertNotBlank("postLogoutUrlParameter", postLogoutUrlParameter);
        }
        this.serverLogoutUrl = serverLogoutUrl;
        this.postLogoutUrlParameter = postLogoutUrlParameter;
    }

    @Override
    public Optional<RedirectionAction> getLogoutAction(final WebContext context, final SessionStore sessionStore,
                                                       final ProfileManagerFactory profileManagerFactory,
                                                       final UserProfile currentProfile, final String targetUrl) {
        if (isBlank(serverLogoutUrl)) {
            return Optional.empty();
        }

        var redirectUrl = serverLogoutUrl;
        if (isNotBlank(targetUrl)) {
            redirectUrl = addParameter(redirectUrl, postLogoutUrlParameter, targetUrl);
        }
        LOGGER.debug("redirectUrl: {}", redirectUrl);
        return Optional.of(HttpActionHelper.buildRedirectUrlAction(context, redirectUrl));
    }
}
