package org.pac4j.oauth.http;

import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.sun.net.httpserver.HttpServer;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.pac4j.core.context.CallContext;
import org.pac4j.oauth.client.OAuth20Client;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.credentials.authenticator.OAuth20Authenticator;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;
import org.pac4j.scribe.builder.api.FigShareApi20;
import org.pac4j.scribe.builder.api.GenericApi20;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests {@link ConfiguringHttpClient}: alone, then behind the requests ScribeJava sends to a local OAuth server.
 */
class ConfiguringHttpClientTests {

    private static final String CALLBACK = "https://client.example/callback";

    private final CallContext ctx = new CallContext(MockWebContext.create(), new MockSessionStore());

    private final List<String> received = new ArrayList<>();

    private HttpServer server;

    private String serverUrl;

    @BeforeEach
    void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            val body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            received.add(exchange.getRequestMethod() + " " + exchange.getRequestURI() + " DPoP="
                + exchange.getRequestHeaders().getFirst("DPoP") + " " + body);
            val response = "{\"access_token\":\"access-token\",\"token_type\":\"bearer\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (val out = exchange.getResponseBody()) {
                out.write(response);
            }
        });
        server.start();
        serverUrl = "http://localhost:" + server.getAddress().getPort();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void configuresACopyOfTheHeadersBeforeSending() throws Exception {
        val delegate = mock(HttpClient.class);
        val response = new Response(200, "OK", Map.of(), "body");
        when(delegate.execute(any(), anyMap(), any(), anyString(), anyString())).thenReturn(response);
        val headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer access-token");
        val client = new ConfiguringHttpClient(delegate, (request, context) -> {
            assertSame(ctx, context);
            assertEquals(Verb.POST, request.verb());
            assertEquals("https://provider.example/token", request.url());
            assertEquals("Bearer access-token", request.headers().get("authorization"));
            request.headers().put("AUTHORIZATION", "DPoP access-token");
            request.headers().put("DPoP", "proof");
        }, ctx);

        assertSame(response, client.execute("agent", headers, Verb.POST, "https://provider.example/token", "payload"));

        assertEquals(Map.of("Authorization", "Bearer access-token"), headers);
        verify(delegate).execute("agent", Map.of("Authorization", "DPoP access-token", "DPoP", "proof"), Verb.POST,
            "https://provider.example/token", "payload");
    }

    @Test
    void configuresTheTokenAndProfileRequestsSentByScribeJava() {
        val config = configuration(new GenericApi20(serverUrl + "/authorize", serverUrl + "/token"));
        val seen = new ArrayList<String>();
        config.setHttpRequestConfigurer((request, context) -> {
            assertSame(ctx, context);
            seen.add(request.verb() + " " + request.url() + " " + request.headers().get("Authorization"));
            request.headers().put("DPoP", "proof");
        });
        val client = client(config);

        val credentials = new OAuth20Credentials("code");
        new OAuth20Authenticator(config, client).validate(ctx, credentials);
        assertEquals("access-token", credentials.toAccessToken().getAccessToken());
        assertTrue(new OAuth20ProfileCreator(config, client).create(ctx, credentials).isPresent());

        assertEquals(2, seen.size());
        assertTrue(seen.get(0).startsWith("POST " + serverUrl + "/token Basic "), seen.get(0));
        assertEquals("GET " + serverUrl + "/profile Bearer access-token", seen.get(1));
        assertEquals(2, received.size());
        assertTrue(received.get(0).startsWith("POST /token DPoP=proof "), received.get(0));
        assertTrue(received.get(0).contains("code=code"), received.get(0));
        assertTrue(received.get(1).startsWith("GET /profile DPoP=proof"), received.get(1));
    }

    @Test
    void keepsTheTokenRequestBuiltByTheProviderService() {
        val api = new FigShareApi20() {
            @Override
            public String getAccessTokenEndpoint() {
                return serverUrl + "/v2/token";
            }
        };
        val config = configuration(api);
        config.setHttpRequestConfigurer((request, context) -> request.headers().put("DPoP", "proof"));

        new OAuth20Authenticator(config, client(config)).validate(ctx, new OAuth20Credentials("code"));

        assertEquals(1, received.size());
        assertTrue(received.get(0).startsWith("POST /v2/token DPoP=proof "), received.get(0));
        assertTrue(received.get(0).contains("client_secret=secret"), received.get(0));
    }

    @Test
    void sendsTheRequestsUntouchedWithoutConfigurer() {
        val config = configuration(new GenericApi20(serverUrl + "/authorize", serverUrl + "/token"));

        new OAuth20Authenticator(config, client(config)).validate(ctx, new OAuth20Credentials("code"));

        assertEquals(1, received.size());
        assertTrue(received.get(0).startsWith("POST /token DPoP=null "), received.get(0));
    }

    @Test
    void usesTheDefinedHttpClientWithAndWithoutConfigurer() throws Exception {
        val httpClient = mock(HttpClient.class);
        val tokenResponse = new Response(200, "OK", Map.of(), "{\"access_token\":\"access-token\"}");
        when(httpClient.execute(any(), anyMap(), any(), anyString(), any(byte[].class))).thenReturn(tokenResponse);
        val config = configuration(new GenericApi20(serverUrl + "/authorize", serverUrl + "/token"));
        config.setHttpClient(httpClient);
        val client = client(config);

        new OAuth20Authenticator(config, client).validate(ctx, new OAuth20Credentials("code"));
        config.setHttpRequestConfigurer((request, context) -> request.headers().put("DPoP", "proof"));
        new OAuth20Authenticator(config, client).validate(ctx, new OAuth20Credentials("code"));

        val headers = ArgumentCaptor.forClass(Map.class);
        verify(httpClient, times(2)).execute(any(), headers.capture(), eq(Verb.POST), eq(serverUrl + "/token"), any(byte[].class));
        assertNull(headers.getAllValues().get(0).get("DPoP"));
        assertEquals("proof", headers.getAllValues().get(1).get("DPoP"));
        assertTrue(received.isEmpty());
    }

    private OAuth20Configuration configuration(final Object api) {
        val config = new OAuth20Configuration();
        config.setApi(api);
        config.setKey("key");
        config.setSecret("secret");
        val definition = mock(OAuthProfileDefinition.class);
        when(definition.getProfileUrl(any(), any())).thenReturn(serverUrl + "/profile");
        when(definition.getProfileVerb()).thenReturn(Verb.GET);
        when(definition.extractUserProfile(anyString())).thenReturn(new OAuth20Profile());
        config.setProfileDefinition(definition);
        return config;
    }

    private OAuth20Client client(final OAuth20Configuration config) {
        val client = new OAuth20Client();
        client.setConfiguration(config);
        client.setCallbackUrl(CALLBACK);
        client.init();
        return client;
    }
}
