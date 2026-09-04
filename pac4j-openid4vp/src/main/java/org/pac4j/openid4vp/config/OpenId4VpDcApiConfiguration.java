package org.pac4j.openid4vp.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.val;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.pac4j.core.util.CommonHelper.assertTrue;

/**
 * The configuration of a verifier reaching the wallet through the digital credentials API of the browser.
 *
 * <p>The wallet is not invoked by a URL and answers through the browser, so there is no response URI and no
 * leg without a session: the page never leaves. What the browser does bring is the authenticated origin of
 * the verifier, which the wallet compares to {@link #expectedOrigins} to detect a replayed request.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
public class OpenId4VpDcApiConfiguration extends OpenId4VpConfiguration {

    /**
     * The origins this verifier makes its requests from, as the browser authenticates them. Mandatory for a
     * signed request: the wallet compares them to the origin it was given, by plain string comparison.
     */
    private List<String> expectedOrigins = new ArrayList<>();

    /**
     * <p>Constructor for OpenId4VpDcApiConfiguration.</p>
     */
    public OpenId4VpDcApiConfiguration() {
        setResponseMode(ResponseMode.DC_API_JWT);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        super.internalInit(forceReinit);

        assertTrue(expectedOrigins != null && !expectedOrigins.isEmpty(),
            "expectedOrigins cannot be empty: a signed request sent over the digital credentials API must tell the wallet "
                + "which origins it may come from");
        expectedOrigins.forEach(origin -> {
            assertTrue(!origin.startsWith("ftp:") && !origin.startsWith("javascript:")
                && !origin.startsWith("data:") && !origin.startsWith("ws:") && !origin.startsWith("wss:"),
                "unsafe scheme for an expected origin: " + origin);
            // the wallet compares it to the browser origin by plain string equality, which never carries a path
            val uri = URI.create(origin);
            assertTrue(uri.getScheme() != null && uri.getHost() != null && (uri.getRawPath() == null || uri.getRawPath().isEmpty())
                && uri.getRawQuery() == null && uri.getRawFragment() == null,
                "an expected origin must be a scheme, a host and an optional port, nothing more: " + origin);
        });
        assertTrue(getClientIdPrefix().isSignedRequest(),
            "the " + getClientIdPrefix().getValue() + " client identifier prefix cannot be used over the digital credentials "
                + "API: its requests cannot be signed, and this binding hands a signed request object to the browser");
    }

    /** {@inheritDoc} */
    @Override
    protected void checkResponseModeBinding() {
        assertTrue(getResponseMode().isOverDcApi(), "the response mode of a digital credentials API request must be "
            + ResponseMode.DC_API.getValue() + " or " + ResponseMode.DC_API_JWT.getValue());
    }
}
