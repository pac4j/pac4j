package org.pac4j.openid4vp.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.credentials.authenticator.OpenId4VpAuthenticator;
import org.pac4j.openid4vp.credentials.extractor.OpenId4VpCredentialsExtractor;
import org.pac4j.openid4vp.profile.OpenId4VpProfileDefinition;
import org.pac4j.openid4vp.profile.ProfileIdResolver;
import org.pac4j.openid4vp.redirect.OpenId4VpRedirectionActionBuilder;
import org.pac4j.openid4vp.request.OpenId4VpRequestObjectBuilder;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.VP_TRANSACTION_ID;

/**
 * This class is the client to authenticate users against a wallet, using OpenID for Verifiable
 * Presentations (OpenID4VP) in its final version 1.0: the application acts as a verifier and asks the
 * wallet to present credentials.
 *
 * <p>There is no identity provider here, no token endpoint and no user info: the wallet presents the
 * credentials directly and the whole validation happens locally.</p>
 *
 * <p>The earlier drafts differ on the wire, a separate {@code client_id_scheme} parameter and the
 * {@code presentation_definition} query language among others, and a wallet built on one of them will not
 * understand these requests.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html">OpenID for Verifiable Presentations 1.0</a>
 * @author Jerome LELEU
 * @since 6.6.0
 */
@ToString(callSuper = true)
public class OpenId4VpClient extends IndirectClient {

    @Getter
    private OpenId4VpConfiguration configuration;

    /** Builds the request object served to the wallet. Replace it to add claims or to answer wallet metadata. */
    @Getter
    @Setter
    private OpenId4VpRequestObjectBuilder requestObjectBuilder;

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
        if (requestObjectBuilder == null) {
            requestObjectBuilder = new OpenId4VpRequestObjectBuilder(this);
        }
        setRedirectionActionBuilderIfUndefined(new OpenId4VpRedirectionActionBuilder(this));
        setCredentialsExtractorIfUndefined(new OpenId4VpCredentialsExtractor(this));
        // the authenticator builds the profile, which the default profile creator of the client returns
        val authenticator = new OpenId4VpAuthenticator(this);
        authenticator.setProfileDefinition(defaultProfileDefinition());
        setAuthenticatorIfUndefined(authenticator);
        if (configuration.getProfileIdResolver() == null) {
            configuration.setProfileIdResolver(defaultProfileIdResolver());
        }
        assertTrue(configuration.getProfileIdResolver() != null, "no profileIdResolver is configured to "
            + "identify the user: set one, such as ProfileIdResolver.issuerAndClaim with a stable claim requested in the DCQL query");

        checkAjaxRequestResolver();

        configuration.init(this.getClass().getSimpleName(), forceReinit);
        logger.debug("OpenID4VP client initialized: {}", this);
    }

    /**
     * <p>The profile definition of the default authenticator.</p>
     *
     * @return a definition of {@code VerifiableCredentialProfile}
     */
    protected OpenId4VpProfileDefinition defaultProfileDefinition() {
        return new OpenId4VpProfileDefinition();
    }

    /**
     * <p>The profile identifier resolver used when the configuration has none: the issuer and the {@code sub}
     * claim of the credential.</p>
     *
     * @return the resolver, or null to require one in the configuration
     */
    protected ProfileIdResolver defaultProfileIdResolver() {
        return ProfileIdResolver.issuerAndClaim("sub");
    }

    /**
     * <p>The URL a wallet fetches the request object from and posts its response to: the regular callback
     * endpoint, qualified by the transaction identifier since those two legs carry no session.</p>
     *
     * @param context the web context
     * @param transactionId the identifier of the transaction
     * @return the request URI
     */
    public String computeRequestUri(final WebContext context, final String transactionId) {
        return CommonHelper.addParameter(computeFinalCallbackUrl(context), VP_TRANSACTION_ID, transactionId);
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
