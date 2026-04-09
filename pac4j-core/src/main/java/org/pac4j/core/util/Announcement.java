package org.pac4j.core.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Announcing next versions.
 *
 * @author Jerome LELEU
 * @since 6.5.0
 */
@Slf4j
public final class Announcement {

    private static boolean displayed = false;

    public Announcement() {
        if (!displayed) {
            LOGGER.warn("\u26A0 UPCOMING VERSIONS:");
            LOGGER.warn("\u26A0 + v7.0.0:");
            LOGGER.warn("\u26A0  - the `pac4j-config` module will be removed");
            LOGGER.warn("\u26A0  - the `pac4j-javaee` module will likely be removed");
            LOGGER.warn("\u26A0  - all deprecated elements will be removed");
            LOGGER.warn("\u26A0  - the 'legcay mode' of the `(Ldap|Db|Mongo)ProfileService` will be removed");
            LOGGER.warn("\u26A0 \u2709 Contact `pac4j-dev@googlegroups.com` for discussions");
            displayed = true;
        }
    }
}
