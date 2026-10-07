package org.pac4j.core.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.test.context.MockWebContext;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ProtocolMessages}.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
final class ProtocolMessagesTests {

    private final ProtocolMessages messages = new ProtocolMessages("TEST");

    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    private Logger logger;

    @BeforeEach
    void captureLogs() {
        logger = (Logger) LoggerFactory.getLogger("PROTOCOL_MESSAGE.TEST");
        logger.setLevel(Level.DEBUG);
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void releaseLogs() {
        logger.detachAppender(appender);
        logger.setLevel(null);
    }

    @Test
    void logsTheDirectionThePartyAndTheMessage() {
        messages.sent("browser", "login URL: https://idp.example.org/login");
        messages.received("tx 1", "wallet", "POST response=abc");

        assertEquals(List.of(">>> browser login URL: https://idp.example.org/login", "[tx 1] <<< wallet POST response=abc"),
            appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList());
        assertTrue(appender.list.stream().allMatch(event -> event.getLevel() == Level.DEBUG));
    }

    @Test
    void logsTheParametersOfARequestSomeBeingMasked() {
        val context = MockWebContext.create().setRequestMethod("POST")
            .addRequestParameter("username", "jdoe").addRequestParameter("password", "secret");

        messages.received(null, "CAS server", context, "password");

        val line = appender.list.get(0).getFormattedMessage();
        assertTrue(line.startsWith("<<< CAS server POST "));
        assertTrue(line.contains("username=jdoe"));
        assertTrue(line.contains("password=" + ProtocolMessages.MASK));
        assertFalse(line.contains("secret"));
    }

    @Test
    void aMessageCannotForgeOtherLines() {
        messages.received("tx\n2", "wallet", "error=x\r\nforged line");

        assertEquals("[tx 2] <<< wallet error=x  forged line", appender.list.get(0).getFormattedMessage());
    }

    @Test
    void nothingIsBuiltWhenTheLoggerIsDisabled() {
        logger.setLevel(Level.INFO);

        messages.sent("browser", "anything");

        assertFalse(messages.isEnabled());
        assertTrue(appender.list.isEmpty());
    }

    @Test
    void masksTheGivenParametersOfAForm() {
        assertEquals("grant_type=authorization_code&client_secret=*****&code=abc",
            ProtocolMessages.maskForm("grant_type=authorization_code&client_secret=s3cr3t&code=abc", "client_secret"));
        assertEquals("a=1&b", ProtocolMessages.maskForm("a=1&b", "c"));
        assertNull(ProtocolMessages.maskForm(null, "c"));
        assertEquals("", ProtocolMessages.maskForm("", "c"));
    }

    @Test
    void masksTheGivenParametersOfAUrl() {
        val url = "https://graph.facebook.com/oauth/access_token?client_id=1&client_secret=";
        assertEquals(url + "*****&grant_type=fb_exchange_token",
            ProtocolMessages.maskUrl(url + "s3cr3t&grant_type=fb_exchange_token", "client_secret"));
        assertEquals("https://example.org/cb?code=abc#state=x",
            ProtocolMessages.maskUrl("https://example.org/cb?code=abc#state=x", "client_secret"));
        assertEquals("https://example.org/cb", ProtocolMessages.maskUrl("https://example.org/cb", "client_secret"));
        assertNull(ProtocolMessages.maskUrl(null, "client_secret"));
    }

    @Test
    void writesParametersAsAForm() {
        assertEquals("username=jdoe&password=*****", ProtocolMessages.formParameters(
            List.of(new String[] {"username", "jdoe"}, new String[] {"password", "secret"}), "password"));
    }
}
