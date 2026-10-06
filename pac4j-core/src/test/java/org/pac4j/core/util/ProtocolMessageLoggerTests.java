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
 * Tests {@link ProtocolMessageLogger}.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
final class ProtocolMessageLoggerTests {

    private final ProtocolMessageLogger messages = new ProtocolMessageLogger("TEST");

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
        assertTrue(line.contains("password=" + ProtocolMessageLogger.MASK));
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
            ProtocolMessageLogger.maskForm("grant_type=authorization_code&client_secret=s3cr3t&code=abc", "client_secret"));
        assertEquals("a=1&b", ProtocolMessageLogger.maskForm("a=1&b", "c"));
        assertNull(ProtocolMessageLogger.maskForm(null, "c"));
        assertEquals("", ProtocolMessageLogger.maskForm("", "c"));
    }

    @Test
    void writesParametersAsAForm() {
        assertEquals("username=jdoe&password=*****", ProtocolMessageLogger.formParameters(
            List.of(new String[] {"username", "jdoe"}, new String[] {"password", "secret"}), "password"));
    }
}
