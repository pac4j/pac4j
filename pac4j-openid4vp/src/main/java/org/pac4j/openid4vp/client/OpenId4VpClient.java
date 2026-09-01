package org.pac4j.openid4vp.client;

import lombok.Getter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.credentials.authenticator.OpenId4VpAuthenticator;
import org.pac4j.openid4vp.credentials.extractor.OpenId4VpCredentialsExtractor;
import org.pac4j.openid4vp.profile.VerifiableCredentialProfile;
import org.pac4j.openid4vp.profile.creator.OpenId4VpProfileCreator;
import org.pac4j.openid4vp.redirect.OpenId4VpRedirectionActionBuilder;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.VP_TRANSACTION_ID;
import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * This class is the client to authenticate users against a wallet, using OpenID for Verifiable
 * Presentations (OpenID4VP): the application acts as a verifier and asks the wallet to present credentials.
 *
 * <p>There is no identity provider here, no token endpoint and no user info: the wallet presents the
 * credentials directly and the whole validation happens locally.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@ToString(callSuper = true)
public class OpenId4VpClient extends IndirectClient {

    @Getter
    private OpenId4VpConfiguration configuration;

    /**
     * <p>Constructor for OpenId4VpClient.</p>
     */
    public OpenId4VpClient() { }

    /**
     * <p>Constructor for OpenId4VpClient.</p>
     *
     * @param configuration a {@link OpenId4VpConfiguration} object
     */
    public OpenId4VpClient(final OpenId4VpConfiguration configuration) {
        setConfiguration(configuration);
    }

    /**
     * <p>Setter for the field <code>configuration</code>.</p>
     *
     * @param configuration a {@link OpenId4VpConfiguration} object
     */
    public void setConfiguration(final OpenId4VpConfiguration configuration) {
        assertNotNull("configuration", configuration);
        this.configuration = configuration;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The wallet URL is returned in a header for the AJAX requests, so that an application willing to
     * display it as a QR code, for a cross device flow, can read it and render it.</p>
     */
    @Override
    protected void beforeInternalInit(final boolean forceReinit) {
        if (getAjaxRequestResolver() == null) {
            val ajaxRequestResolver = new DefaultAjaxRequestResolver();
            ajaxRequestResolver.setAddRedirectionUrlAsHeader(true);
            setAjaxRequestResolver(ajaxRequestResolver);
        }
        super.beforeInternalInit(forceReinit);
        assertNotNull("configuration", configuration);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        setRedirectionActionBuilderIfUndefined(new OpenId4VpRedirectionActionBuilder(this));
        setCredentialsExtractorIfUndefined(new OpenId4VpCredentialsExtractor(this));
        setAuthenticatorIfUndefined(new OpenId4VpAuthenticator(this));
        setProfileCreatorIfUndefined(new OpenId4VpProfileCreator(this,
            new CommonProfileDefinition(x -> new VerifiableCredentialProfile())));

        checkAjaxRequestResolver();

        configuration.init(this.getClass().getSimpleName(), forceReinit);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Skipped for the two legs coming from the wallet. They carry no session, so there is nothing to
     * remember and nothing worth creating a session for; and the flag would make the next browser navigation
     * answer an unauthorized response although nothing failed.</p>
     *
     * <p>Both legs are told apart by the transaction identifier they carry, which a browser navigation to the
     * callback never does.</p>
     */
    @Override
    protected void saveAttemptedAuthentication(final WebContext context, final SessionStore sessionStore) {
        if (context.getRequestParameter(VP_TRANSACTION_ID).isEmpty()) {
            super.saveAttemptedAuthentication(context, sessionStore);
        }
    }

    /**
     * <p>Check the AJAX request resolver hands the wallet URL over to the application, which is what allows
     * a QR code to be displayed for a cross device flow.</p>
     *
     * <p>With the default resolver, this is not a matter of taste: the redirection action builder is only
     * called when the URL is asked for, and it is the builder which opens the transaction. Leaving the
     * property to false silently breaks the flow rather than degrading it.</p>
     *
     * <p>Override this to plug a resolver of your own, which must call the redirection action builder and
     * return its URL to the caller, in whatever form suits your application.</p>
     */
    protected void checkAjaxRequestResolver() {
        if (getAjaxRequestResolver() instanceof DefaultAjaxRequestResolver defaultAjaxRequestResolver) {
            assertTrue(defaultAjaxRequestResolver.isAddRedirectionUrlAsHeader(),
                "the addRedirectionUrlAsHeader property of the DefaultAjaxRequestResolver must be true: the wallet URL must be "
                    + "returned to the application and the redirection action builder must run to open the transaction");
        }
    }
}
