package org.pac4j.openid4vp.util;

/**
 * The OpenID4VP constants.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
public interface OpenId4VpConstants {

    /** The only response type defined by OpenID4VP. */
    String RESPONSE_TYPE_VP_TOKEN = "vp_token";

    /** Authorization request parameters. */
    String CLIENT_ID = "client_id";
    String RESPONSE_TYPE = "response_type";
    String RESPONSE_MODE = "response_mode";
    String RESPONSE_URI = "response_uri";
    String REQUEST_URI = "request_uri";
    String REQUEST_URI_METHOD = "request_uri_method";
    String REQUEST_URI_METHOD_POST = "post";
    String NONCE = "nonce";
    String STATE = "state";
    String DCQL_QUERY = "dcql_query";
    String SCOPE = "scope";
    String CLIENT_METADATA = "client_metadata";
    String VERIFIER_INFO = "verifier_info";

    /** The members of an attestation of the verifier_info parameter. */
    String FORMAT = "format";
    String DATA = "data";
    String CREDENTIAL_IDS = "credential_ids";

    /**
     * The parameters a wallet may post to the request URI before getting the request object, when the
     * request URI method is post: what it supports, and a nonce the request object must carry back.
     */
    String WALLET_METADATA = "wallet_metadata";
    String WALLET_NONCE = "wallet_nonce";

    /** Authorization response parameters. */
    String VP_TOKEN = "vp_token";
    String RESPONSE = "response";
    String RESPONSE_CODE = "response_code";
    /** The error response parameters, RFC 6749 as OpenID4VP clarifies them. */
    String ERROR = "error";
    String ERROR_DESCRIPTION = "error_description";

    /**
     * The audience of a request object when the wallet is not identified beforehand: "a symbolic string
     * [which] can be used as an aud claim value even when this specification is used standalone, without SIOPv2".
     */
    String REQUEST_OBJECT_AUDIENCE = "https://self-issued.me/v2";

    /** The "typ" header and content type of a signed request object (JAR). */
    String REQUEST_OBJECT_TYPE = "oauth-authz-req+jwt";
    String REQUEST_OBJECT_CONTENT_TYPE = "application/oauth-authz-req+jwt";

    /**
     * The parameter carrying the transaction identifier on the two wallet legs (fetching the request object
     * and posting the response). Both legs reach the regular pac4j callback endpoint without any session.
     */
    String VP_TRANSACTION_ID = "vp_tx";

    /** The session attribute holding the identifier of the pending transaction, on the browser side. */
    String SESSION_TRANSACTION_ID = "$openid4vpTransactionId";

    /** The parameters of a request sent over the digital credentials API. */
    String EXPECTED_ORIGINS = "expected_origins";
    String REQUEST = "request";

    /** The default custom scheme used to invoke a wallet on the same device. */
    String OPENID4VP_SCHEME = "openid4vp://";

    /**
     * The members of the client metadata. Beware: these names changed more than once across the drafts,
     * check them against the version of the specification actually targeted.
     */
    String JWKS = "jwks";
    String KEYS = "keys";
    String VP_FORMATS_SUPPORTED = "vp_formats_supported";
    String ENCRYPTED_RESPONSE_ENC_VALUES_SUPPORTED = "encrypted_response_enc_values_supported";
    /** The wallet metadata member naming the content encryption algorithms it encrypts its response with. */
    String AUTHORIZATION_ENCRYPTION_ENC_VALUES_SUPPORTED = "authorization_encryption_enc_values_supported";
}
