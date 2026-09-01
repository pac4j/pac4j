package org.pac4j.openid4vp.wallet;

import com.nimbusds.jose.jwk.ECKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * A presentation request, as a wallet reads it out of the request object.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@RequiredArgsConstructor
@ToString(exclude = "encryptionKey")
public class WalletRequest {

    private final String clientId;

    private final String nonce;

    /** Where the response must be posted: in pac4j, the very URL the request object was fetched from. */
    private final String responseUri;

    /** The public key the response must be encrypted to, null when the response is not encrypted. */
    private final ECKey encryptionKey;

    private final Map<String, Object> dcqlQuery;
}
