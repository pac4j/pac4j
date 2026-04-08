package org.pac4j.core.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Announcing next versions.
 *
 * @author Jerome LELEU
 * @since 6.5.0
 */
@Slf4j
public final class Announce {

    private static boolean displayed = false;

    public Announce() {
        if (!displayed) {
            LOGGER.warn("\u26A0 UPCOMING VERSIONS:");
            LOGGER.warn("\u26A0 + v6.5.0:");
            LOGGER.warn("\u26A0  - the `pac4j-gae`, `pac4j-couch` and `pac4j-springboot` modules will be removed");
            LOGGER.warn("\u26A0  - the OSGi and shading Maven phases will be removed");
            LOGGER.warn("\u26A0  - the JSON serializer is the default one for `(Ldap|Db|Mongo)ProfileService`");
            LOGGER.warn("\u26A0 + v7.0.0:");
            LOGGER.warn("\u26A0  - the `pac4j-config` module will be removed");
            LOGGER.warn("\u26A0  - the `pac4j-javaee` module will likely be removed");
            LOGGER.warn("\u26A0  - all deprecated elements will be removed");
            LOGGER.warn("\u26A0  - the 'legcay mode' of the `(Ldap|Db|Mongo)ProfileService` will be removed");
            displayed = true;
        }
    }
}
