package org.pac4j.oidc.logout;

import com.nimbusds.jwt.JWT;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.exception.http.TemporaryRedirectAction;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Logout action builder for OpenID Connect.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OidcLogoutActionBuilder implements LogoutActionBuilder {

    protected OidcConfiguration configuration;

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    public OidcLogoutActionBuilder(final OidcConfiguration configuration) {
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
    }

    @Override
    public RedirectionAction getLogoutAction(final WebContext context, final UserProfile currentProfile, final String targetUrl) {
        final String logoutUrl = configuration.getLogoutUrl();
        if (CommonHelper.isNotBlank(logoutUrl) && currentProfile instanceof OidcProfile) {
            try {
                final URI endSessionEndpoint = new URI(logoutUrl);
                final JWT idToken = ((OidcProfile) currentProfile).getIdToken();

                LogoutRequest logoutRequest;
                if (CommonHelper.isNotBlank(targetUrl)) {
                    logoutRequest = new LogoutRequest(endSessionEndpoint, idToken, new URI(targetUrl), null);
                } else {
                    logoutRequest = new LogoutRequest(endSessionEndpoint, idToken);
                }

                if (ajaxRequestResolver.isAjax(context)) {
                    context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, "");
                    context.setResponseHeader(HttpConstants.LOCATION_HEADER, logoutRequest.toURI().toString());
                    throw ForbiddenAction.INSTANCE;
                }

                return new TemporaryRedirectAction(logoutRequest.toURI().toString());
            } catch (final URISyntaxException e) {
                throw new TechnicalException(e);
            }
        }

        return null;
    }

    public AjaxRequestResolver getAjaxRequestResolver() {
        return ajaxRequestResolver;
    }

    public void setAjaxRequestResolver(final AjaxRequestResolver ajaxRequestResolver) {
        CommonHelper.assertNotNull("ajaxRequestResolver", ajaxRequestResolver);
        this.ajaxRequestResolver = ajaxRequestResolver;
    }
}
