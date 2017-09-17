package org.pac4j.oidc.logout;

import com.nimbusds.jwt.JWT;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.http.AjaxRequestResolver;
import org.pac4j.core.http.DefaultAjaxRequestResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
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
public class OidcLogoutActionBuilder<U extends OidcProfile> extends InitializableWebObject implements LogoutActionBuilder<U> {

    private OidcConfiguration configuration;

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    public OidcLogoutActionBuilder() {}

    public OidcLogoutActionBuilder(final OidcConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", configuration);
        configuration.init(context);
    }

    @Override
    public RedirectAction getLogoutAction(final WebContext context, final U currentProfile, final String targetUrl) {
        init(context);
        final String logoutUrl = configuration.getLogoutUrl();
        if (CommonHelper.isNotBlank(logoutUrl)) {
            try {
                final URI endSessionEndpoint = new URI(logoutUrl);
                final JWT idToken = currentProfile.getIdToken();

                LogoutRequest logoutRequest;
                if (CommonHelper.isNotBlank(targetUrl)) {
                    logoutRequest = new LogoutRequest(endSessionEndpoint, idToken, new URI(targetUrl), null);
                } else {
                    logoutRequest = new LogoutRequest(endSessionEndpoint, idToken);
                }

                if (ajaxRequestResolver.isAjax(context)) {
                  context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, "");
                  context.setResponseHeader(HttpConstants.LOCATION_HEADER, logoutRequest.toURI().toString());
                  throw HttpAction.status("AJAX request -> 403", 403, context);
                }

                return RedirectAction.redirect(logoutRequest.toURI().toString());
            } catch (final URISyntaxException e) {
                throw new TechnicalException(e);
            }
        }

        return null;
    }

    public void setConfiguration(OidcConfiguration configuration) {
        this.configuration = configuration;
    }

    public OidcConfiguration getConfiguration() {
        return this.configuration;
    }

    public AjaxRequestResolver getAjaxRequestResolver() {
        return ajaxRequestResolver;
    }

    public void setAjaxRequestResolver(final AjaxRequestResolver ajaxRequestResolver) {
        this.ajaxRequestResolver = ajaxRequestResolver;
    }
}
