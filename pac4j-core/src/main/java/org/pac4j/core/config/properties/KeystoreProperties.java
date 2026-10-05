package org.pac4j.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.pac4j.core.keystore.generation.KeystoreGenerator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.time.Period;

/**
 * Keystore properties.
 *
 * @author Jerome Leleu
 * @since 6.4.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class KeystoreProperties extends ResourceProperties {

    private String keystorePassword;

    private String privateKeyPassword;

    private String keyStoreAlias;

    private String keyStoreType;

    private boolean forceKeystoreGeneration;

    private String certificateNameToAppend;

    private String certificatePrefix;

    private Period certificateExpirationPeriod;

    private String certificateSignatureAlg = "SHA256WithRSA";

    private int privateKeySize = 2048;

    private KeystoreGenerator keystoreGenerator;

    /**
     * <p>Properties without keystore yet.</p>
     */
    public KeystoreProperties() {
        // the resource is set later
    }

    /**
     * <p>Properties of the keystore at a path.</p>
     *
     * @param path the path: {@code classpath:}, {@code file:}, {@code http(s)://} or a plain file path
     */
    public KeystoreProperties(final String path) {
        super(path);
    }

    /** {@inheritDoc} */
    @Override
    public KeystoreProperties setResource(final Resource resource) {
        super.setResource(resource);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public KeystoreProperties setPath(final String path) {
        super.setPath(path);
        return this;
    }

    /**
     * <p>The keystore resource.</p>
     *
     * @return the resource
     * @deprecated use {@code getResource()}
     */
    @Deprecated
    public Resource getKeystoreResource() {
        return getResource();
    }

    /**
     * <p>Set the keystore resource.</p>
     *
     * @param resource the resource
     * @return the properties
     * @deprecated use {@link #setResource(Resource)}
     */
    @Deprecated
    public KeystoreProperties setKeystoreResource(final Resource resource) {
        return setResource(resource);
    }

    /**
     * <p>Set the keystore resource from its path.</p>
     *
     * @param path the path
     * @return the properties
     * @deprecated use {@link #setPath(String)}
     */
    @Deprecated
    public KeystoreProperties setKeystorePath(final String path) {
        return setPath(path);
    }

    @Deprecated
    public void setKeystoreResourceFilepath(final String path) {
        setResource(new FileSystemResource(path));
    }

    @Deprecated
    public void setKeystoreResourceClasspath(final String path) {
        setResource(new ClassPathResource(path));
    }

    @Deprecated
    public void setKeystoreResourceUrl(final String url) {
        setPath(url);
    }
}
