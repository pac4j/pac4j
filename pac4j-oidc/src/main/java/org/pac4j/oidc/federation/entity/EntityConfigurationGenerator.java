package org.pac4j.oidc.federation.entity;

/**
 * Entity configuration generator.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public interface EntityConfigurationGenerator {

    String getContentType();

    String generate();
}
