package org.pac4j.openid4vp.client;

import lombok.ToString;
import org.pac4j.openid4vp.config.OpenId4VpDcApiConfiguration;
import org.pac4j.openid4vp.credentials.extractor.DcApiCredentialsExtractor;
import org.pac4j.openid4vp.redirect.DcApiRedirectionActionBuilder;
import org.pac4j.openid4vp.request.DcApiRequestObjectBuilder;

import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * This class is the client to authenticate users against a wallet reached through the digital credentials
 * API of the browser, rather than by a URL.
 *
 * <p>The browser mediates the whole exchange: it asks the End-User which wallet to use, hands it the request
 * along with the origin it authenticated, and returns the answer to the page. Nothing is posted between the
 * wallet and this application, so the flow never leaves the session and the phishing resistance comes from
 * the origin rather than from a scheme any application could claim.</p>
 *
 * <p>It builds the very same signed request object as {@link OpenId4VpClient}, only the claims tied to the
 * way the wallet is reached differ, so the protocol is {@code openid4vp-v1-signed}.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@ToString(callSuper = true)
public class OpenId4VpDcApiClient extends OpenId4VpClient {

    /**
     * <p>Constructor for OpenId4VpDcApiClient.</p>
     */
    public OpenId4VpDcApiClient() { }

    /**
     * <p>Constructor for OpenId4VpDcApiClient.</p>
     *
     * @param configuration a {@link OpenId4VpDcApiConfiguration} object
     */
    public OpenId4VpDcApiClient(final OpenId4VpDcApiConfiguration configuration) {
        super(configuration);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertTrue(getConfiguration() instanceof OpenId4VpDcApiConfiguration,
            "the configuration of an " + getClass().getSimpleName() + " must be an "
                + OpenId4VpDcApiConfiguration.class.getSimpleName());

        setRequestObjectBuilder(new DcApiRequestObjectBuilder(this));
        setRedirectionActionBuilderIfUndefined(new DcApiRedirectionActionBuilder(this));
        setCredentialsExtractorIfUndefined(new DcApiCredentialsExtractor(this));

        super.internalInit(forceReinit);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Nothing to check here: this client hands the request object over as a JSON body, never as a URL in a
     * header. The page must therefore not mark its call as an AJAX one, or the resolver would answer an empty
     * unauthorized response and drop that body.</p>
     */
    @Override
    protected void checkAjaxRequestResolver() {
        // no URL is ever handed over, so there is nothing to hand over in a header
    }
}
