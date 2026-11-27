package org.pac4j.jee.context.session;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import javax.servlet.http.HttpSession;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class JEESessionStoreTest {

    @Test
    void masksOnlyThePrivateKeyOnWriteAndRead() {
        val key = "pac4jDPoPKey|ES256";
        val nonceKey = "pac4jDPoPNonce|https://op.example";
        val privateKey = "{\"kty\":\"EC\",\"d\":\"private-test-value\"}";
        val session = mock(HttpSession.class);
        val store = new JEESessionStore(session);
        store.setPrefix("app.");
        when(session.getAttribute("app." + key)).thenReturn(privateKey);
        when(session.getAttribute("app." + nonceKey)).thenReturn("test-nonce");

        val logger = (Logger) LoggerFactory.getLogger(JEESessionStore.class);
        val previousLevel = logger.getLevel();
        val appender = new ListAppender<ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);
        try {
            logger.setLevel(Level.DEBUG);
            store.set(null, key, privateKey);
            verify(session).setAttribute("app." + key, privateKey);
            assertEquals(privateKey, store.get(null, key).orElseThrow());
            store.set(null, nonceKey, "test-nonce");
            assertEquals("test-nonce", store.get(null, nonceKey).orElseThrow());

            val messages = appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
            assertTrue(messages.contains("Set key: app." + key + " for value: *****"));
            assertTrue(messages.contains("Get value: ***** for key: app." + key));
            assertTrue(messages.contains("Set key: app." + nonceKey + " for value: test-nonce"));
            assertTrue(messages.contains("Get value: test-nonce for key: app." + nonceKey));
            assertTrue(messages.stream().noneMatch(message -> message.contains("private-test-value")));
        } finally {
            logger.detachAppender(appender);
            logger.setLevel(previousLevel);
            appender.stop();
        }
    }
}
