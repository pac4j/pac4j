package org.pac4j.oidc.config.method;

import com.nimbusds.jose.JWSAlgorithm;
import lombok.*;

import java.net.URI;

/**
 * The configuration for the client authentication method: client_secret_jwt.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClientSecretJwtClientAuthnMethodConfig {

    private URI audience;

    private JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;
}
