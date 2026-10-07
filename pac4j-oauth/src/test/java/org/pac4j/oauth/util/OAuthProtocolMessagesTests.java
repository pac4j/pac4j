package org.pac4j.oauth.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.test.context.MockWebContext;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests {@link OAuthProtocolMessages}.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
final class OAuthProtocolMessagesTests {

    private static final String TOKEN_URL = "https://oauth.example.org/token";

    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    private Logger logger;

    @BeforeEach
    void captureLogs() {
        logger = (Logger) LoggerFactory.getLogger("PROTOCOL_MESSAGE.OAUTH");
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
    void logsTheMessagesExchangedWithTheBrowser() {
        OAuthProtocolMessages.sentToBrowser("authorization URL: https://oauth.example.org/authorize?client_id=1");
        OAuthProtocolMessages.receivedFromBrowser(MockWebContext.create().setRequestMethod("GET")
            .addRequestParameter("code", "abc").addRequestParameter("state", "xyz"));
        assertEquals(List.of(">>> browser authorization URL: https://oauth.example.org/authorize?client_id=1",
            "<<< browser GET code=abc&state=xyz"), lines());
    }

    @Test
    void logsTheRequestsSentByScribeJavaTheClientSecretBeingMasked() throws Exception {
        val delegate = mock(HttpClient.class);
        val response = new Response(200, "OK", Map.of(), "{\"access_token\":\"tok\"}");
        when(delegate.execute(any(), any(), eq(Verb.POST), eq(TOKEN_URL), any(byte[].class))).thenReturn(response);
        assertNotNull(OAuthProtocolMessages.httpClient((HttpClientConfig) null));
        val client = OAuthProtocolMessages.httpClient(delegate);

        val body = "grant_type=authorization_code&client_secret=s3cr3t&code=abc".getBytes(StandardCharsets.UTF_8);
        assertSame(response, client.execute("pac4j", Map.of(), Verb.POST, TOKEN_URL, body));

        assertEquals(List.of(">>> OAuth server POST " + TOKEN_URL + " grant_type=authorization_code&client_secret=*****&code=abc",
            "<<< OAuth server 200 {\"access_token\":\"tok\"}"), lines());
    }

    @Test
    void masksTheClientSecretOfAQueryString() throws Exception {
        val delegate = mock(HttpClient.class);
        val url = "https://graph.facebook.com/oauth/access_token?client_id=1&client_secret=s3cr3t&grant_type=fb_exchange_token";
        when(delegate.execute(any(), any(), eq(Verb.GET), eq(url), (byte[]) any())).thenReturn(new Response(200, "OK", Map.of(), ""));
        OAuthProtocolMessages.httpClient(delegate).execute("pac4j", Map.of(), Verb.GET, url, (byte[]) null);
        assertEquals(">>> OAuth server GET https://graph.facebook.com/oauth/access_token?client_id=1&client_secret=*****"
            + "&grant_type=fb_exchange_token", lines().get(0));
    }

    @Test
    void scribeJavaBuildsItsOwnClientWhenTheMessagesAreNotLogged() {
        logger.setLevel(Level.INFO);
        assertNull(OAuthProtocolMessages.httpClient((HttpClientConfig) null));
    }

    private List<String> lines() {
        return appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }
}
