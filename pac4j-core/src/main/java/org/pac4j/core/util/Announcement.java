package org.pac4j.core.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Announcing next versions.
 *
 * @author Jerome LELEU
 * @since 6.4.3
 */
@Slf4j
@RequiredArgsConstructor
public final class Announcement {

    @Getter(AccessLevel.PACKAGE)
    private boolean announced;

    private final String version;

    private final String message;

    public Announcement announce() {
        if (!announced) {
            LOGGER.warn("\u26A0 In version {}, {}", version, message);
            announced = true;
        }
        return this;
    }
}
