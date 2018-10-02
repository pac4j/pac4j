package org.pac4j.saml.util;

/**
 * A Java service provider API hook to allow configuration of OpenSAML.
 *
 * Implementations should include a {@link javax.annotation.Priority} annotation. The lowest priority implementation
 * found is the one used.
 *
 * @see java.util.ServiceLoader
 *
 * @since 3.2.0
 */
public interface ConfigurationManager {
    void configure();
}
