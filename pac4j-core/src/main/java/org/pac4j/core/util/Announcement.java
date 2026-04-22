package org.pac4j.core.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.pac4j.core.util.Pac4jConstants.EMPTY_STRING;

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

    private final boolean mailing;

    public Announcement(final String message) {
        this(null, message, false);
    }

    public Announcement announce() {
        if (!announced) {
            var contact = " (\u2709 contact `pac4j-dev@googlegroups.com` for discussions)";
            if (!mailing) {
                contact = EMPTY_STRING;
            }
            var inversion = EMPTY_STRING;
            if (version != null) {
                inversion = "In version " + version + ", ";
            }
            LOGGER.warn("\u26A0 {}{}{}", inversion, message, contact);
            announced = true;
        }
        return this;
    }
}
