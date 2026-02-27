package org.pac4j.oidc.federation.config;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.keystore.generation.FileSystemKeystoreGenerator;
import org.pac4j.oidc.federation.entity.EntityConfigurationGenerator;

import java.time.Period;
import java.util.List;

/**
 * Properties dedicated to the federation.
 * https://openid.net/specs/openid-federation-1_0.html
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Getter
@Setter
public class OidcFederationProperties {

    private KeystoreProperties keystore = new KeystoreProperties();

    private JwksProperties jwks = new JwksProperties();

    private EntityConfigurationGenerator entityConfigurationGenerator;

    private int validityInDays = 365;

    private String entityId;

    private String applicationType = "web"; // or native

    private List<String> responseTypes = List.of("code");

    private List<String> grantTypes = List.of("authorization_code");

    private List<String> scopes = List.of("openid", "email", "profile");

    private ClientAuthenticationMethod clientAuthenticationMethod = ClientAuthenticationMethod.PRIVATE_KEY_JWT;

    public OidcFederationProperties() {
        keystore.setCertificatePrefix("oidcfede-signing-cert");
        keystore.setCertificateExpirationPeriod(Period.ofYears(1));
        keystore.setKeystoreGenerator(new FileSystemKeystoreGenerator(keystore));
    }
}
