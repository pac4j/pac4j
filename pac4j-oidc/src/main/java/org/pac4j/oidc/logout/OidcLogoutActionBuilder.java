package org.pac4j.oidc.logout;

import com.nimbusds.openid.connect.sdk.LogoutRequest;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.profile.OidcProfile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Logout action builder for OpenID Connect.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OidcLogoutActionBuilder implements LogoutActionBuilder {

    protected OidcConfiguration configuration;

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    /**
     * <p>Constructor for OidcLogoutActionBuilder.</p>
     *
     * @param configuration a {@link OidcConfiguration} object
     */
    public OidcLogoutActionBuilder(final OidcConfiguration configuration) {
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<RedirectionAction> getLogoutAction(final CallContext ctx, final UserProfile currentProfile, final String targetUrl) {
        val logoutUrl = configuration.findLogoutUrl();
        if (CommonHelper.isNotBlank(logoutUrl) && currentProfile instanceof OidcProfile) {
            try {
                val endSessionEndpoint = new URI(logoutUrl);
                val idToken = ((OidcProfile) currentProfile).getIdToken();

                LogoutRequest logoutRequest;
                if (CommonHelper.isNotBlank(targetUrl)) {
                    logoutRequest = new LogoutRequest(endSessionEndpoint, idToken, new URI(targetUrl), null);
                } else {
                    logoutRequest = new LogoutRequest(endSessionEndpoint, idToken);
                }

                val webContext = ctx.webContext();
                if (ajaxRequestResolver.isAjax(ctx)) {
                    ctx.sessionStore().set(webContext, Pac4jConstants.REQUESTED_URL, null);
                    webContext.setResponseHeader(HttpConstants.LOCATION_HEADER, logoutRequest.toURI().toString());
                    throw new ForbiddenAction();
                }

                return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, logoutRequest.toURI().toString()));
            } catch (final URISyntaxException e) {
                throw new OidcException(e);
            }
        }

        return Optional.empty();
    }

    /**
     * <p>Getter for the field <code>ajaxRequestResolver</code>.</p>
     *
     * @return a {@link AjaxRequestResolver} object
     */
    public AjaxRequestResolver getAjaxRequestResolver() {
        return ajaxRequestResolver;
    }

    /**
     * <p>Setter for the field <code>ajaxRequestResolver</code>.</p>
     *
     * @param ajaxRequestResolver a {@link AjaxRequestResolver} object
     */
    public void setAjaxRequestResolver(final AjaxRequestResolver ajaxRequestResolver) {
        CommonHelper.assertNotNull("ajaxRequestResolver", ajaxRequestResolver);
        this.ajaxRequestResolver = ajaxRequestResolver;
    }
}
