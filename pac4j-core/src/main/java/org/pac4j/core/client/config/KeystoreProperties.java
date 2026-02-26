package org.pac4j.core.client.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.pac4j.core.resource.SpringResourceHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.time.Period;

/**
 * Keystore properties shared by client configurations.
 *
 * @author Jerome Leleu
 * @since 6.4.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class KeystoreProperties {

    private Resource keystoreResource;

    private String keystorePassword;

    private String privateKeyPassword;

    private String keyStoreAlias;

    private String keyStoreType;

    private boolean forceKeystoreGeneration;

    private String certificateNameToAppend;

    private String certificatePrefix;

    private Period certificateExpirationPeriod = Period.ofYears(20);

    private String certificateSignatureAlg = "SHA1WithRSA";

    private int privateKeySize = 2048;

    /**
     * <p>setKeystoreResourceFilepath.</p>
     *
     * @param path a {@link String} object
     */
    public void setKeystoreResourceFilepath(final String path) {
        this.keystoreResource = new FileSystemResource(path);
    }

    /**
     * <p>setKeystoreResourceClasspath.</p>
     *
     * @param path a {@link String} object
     */
    public void setKeystoreResourceClasspath(final String path) {
        this.keystoreResource = new ClassPathResource(path);
    }

    /**
     * <p>setKeystoreResourceUrl.</p>
     *
     * @param url a {@link String} object
     */
    public void setKeystoreResourceUrl(final String url) {
        this.keystoreResource = SpringResourceHelper.buildResourceFromPath(url);
    }

    /**
     * <p>setKeystorePath.</p>
     *
     * @param path a {@link String} object
     */
    public void setKeystorePath(final String path) {
        this.keystoreResource = SpringResourceHelper.buildResourceFromPath(path);
    }
}
