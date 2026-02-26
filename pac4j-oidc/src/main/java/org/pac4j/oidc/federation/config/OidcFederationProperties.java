package org.pac4j.oidc.federation.config;

import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.client.config.KeystoreProperties;
import org.pac4j.core.keystore.generation.FileSystemKeystoreGenerator;
import org.pac4j.core.resource.SpringResourceHelper;
import org.pac4j.oidc.federation.entity.DefaultEntityConfigurationGenerator;
import org.pac4j.oidc.federation.entity.EntityConfigurationGenerator;
import org.springframework.core.io.Resource;

import java.time.Period;

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

    private Resource jwksResource;

    private EntityConfigurationGenerator entityConfigurationGenerator = new DefaultEntityConfigurationGenerator(this);

    public OidcFederationProperties() {
        keystore.setCertificatePrefix("oidcfede-signing-cert");
        keystore.setCertificateExpirationPeriod(Period.ofYears(1));
        keystore.setKeystoreGenerator(new FileSystemKeystoreGenerator(keystore));
    }

    public void setJwksPath(final String path) {
        this.jwksResource = SpringResourceHelper.buildResourceFromPath(path);
    }
}
