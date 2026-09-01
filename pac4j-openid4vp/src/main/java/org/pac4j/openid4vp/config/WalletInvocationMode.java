package org.pac4j.openid4vp.config;

/**
 * Which URL is handed over to the application, the request being always given as a URL and never as a page.
 *
 * <p>This does not say whether the wallet sits on the same device or on another one: a custom scheme URL is
 * followed as a deep link on the same device, and displayed as a QR code on another one. That choice belongs
 * to the application, which is the one rendering anything.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
public enum WalletInvocationMode {

    /**
     * The request URI alone. The browser digital credentials API is driven from JavaScript, so the
     * application fetches the request object from that URI and passes it to the API itself.
     */
    DIGITAL_CREDENTIALS_API,
    /**
     * A custom scheme URL carrying the verifier identifier and the request URI, which a wallet answers to.
     * The wallet fetches the request object itself.
     */
    CUSTOM_SCHEME
}
