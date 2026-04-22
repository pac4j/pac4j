package org.pac4j.core.util;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Announcement}.
 *
 * @author Jerome LELEU
 * @since 6.4.3
 */
public class AnnouncementTests implements TestsConstants {

    @Test
    public void test() {
        val announcement = new Announcement("X", "msg");
        assertFalse(announcement.isAnnounced());
        announcement.announce();
        assertTrue(announcement.isAnnounced());
    }
}
