package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.apis.OdnoklassnikiApi;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.services.TimestampService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.oauth.client.OAuth20Client;
import org.pac4j.oauth.client.CronofyClient;
import org.pac4j.oauth.client.FigShareClient;
import org.pac4j.oauth.client.WechatClient;
import org.pac4j.oauth.profile.wechat.WechatProfileCreator;
import org.pac4j.scribe.model.WechatToken;
import org.pac4j.scribe.service.CronofyService;
import org.pac4j.scribe.service.WechatService;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;
import org.pac4j.scribe.builder.api.*;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Tests provider compatibility and customization before request serialization. */
class TokenRequestBuilderTests {

    private static final String CALLBACK = "https://client.example/callback";
    private static final String PROFILE = "https://provider.example/profile";

    private final CallContext ctx = new CallContext(MockWebContext.create(), new MockSessionStore());

    @Test
    void customBuilderPreservesProviderTokenRequests() throws Exception {
        val generic = new GenericApi20("https://provider.example/authorize", "https://provider.example/token");
        val getApi = new GenericApi20("https://provider.example/authorize", "https://provider.example/token");
        getApi.setAccessTokenVerb(Verb.GET);
        for (val api : List.of(generic, getApi, GoogleApi20.instance(), FacebookApi.instance(), OdnoklassnikiApi.instance(),
            new CronofyApi20("de"), new FigShareApi20(), new WechatApi20(), new QQApi20())) {
            val requests = new ArrayList<List<Object>>();
            val transport = capturingTransport(requests);
            val service = service(api, transport);
            // Capture the legacy request at the transport boundary, before response extraction.
            assertThrows(IOException.class, () -> service.getAccessToken("code +/="));
            val config = configuration(api);
            config.setTokenRequestBuilder(new OAuth20TokenRequestBuilder() {
                @Override
                public OAuthRequest build(final CallContext context, final OAuth20Credentials credentials,
                                          final OAuth20Service providerService) {
                    val request = super.build(context, credentials, providerService);
                    request.addHeader("DPoP", "proof");
                    return request;
                }
            });
            val request = config.getTokenRequestBuilder().build(ctx, new OAuth20Credentials("code +/="), service);
            assertThrows(IOException.class, () -> service.execute(request));
            assertEquals(2, requests.size());
            val originalHeaders = (Map<String, String>) requests.get(0).get(0);
            originalHeaders.put("DPoP", "proof");
            assertEquals(requests.get(0), requests.get(1), api.getClass().getSimpleName());
        }
    }

    @Test
    void sendsCronofyJsonWithProviderService() throws Exception {
        val api = new CronofyApi20("de");
        val requests = new ArrayList<List<Object>>();
        val service = service(api, capturingTransport(requests));
        assertInstanceOf(CronofyService.class, service);
        val request = configuration(api).getTokenRequestBuilder().build(ctx, new OAuth20Credentials("code +/="), service);
        assertThrows(IOException.class, () -> service.execute(request));

        assertEquals(1, requests.size());
        val sent = requests.get(0);
        assertEquals(Map.of("Content-Type", "application/json; charset=utf-8"), sent.get(0));
        assertEquals(Verb.POST, sent.get(1));
        assertEquals("https://app-de.cronofy.com/oauth/token", sent.get(2));
        assertEquals(Map.of("client_id", "key", "client_secret", "secret", "grant_type", "authorization_code",
            "code", "code +/=", "redirect_uri", CALLBACK), JsonHelper.getMapper().readValue((String) sent.get(3), Map.class));
    }

    @Test
    void sendsFigShareCredentialsWithProviderService() throws Exception {
        val api = new FigShareApi20();
        val requests = new ArrayList<List<Object>>();
        val service = service(api, capturingTransport(requests));
        assertInstanceOf(FigShareApi20.Service.class, service);
        val request = configuration(api).getTokenRequestBuilder().build(ctx, new OAuth20Credentials("code +/="), service);
        assertThrows(IOException.class, () -> service.execute(request));

        assertEquals(1, requests.size());
        val sent = requests.get(0);
        assertEquals(Map.of("Authorization", "Basic a2V5OnNlY3JldA=="), sent.get(0));
        assertEquals(Verb.POST, sent.get(1));
        assertEquals("https://api.figshare.com/v2/token", sent.get(2));
        val parameters = new HashMap<String, String>();
        for (val parameter : ((String) sent.get(3)).split("&")) {
            val pair = parameter.split("=", 2);
            parameters.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
        }
        assertEquals(Map.of("client_id", "key", "client_secret", "secret", "grant_type", "authorization_code",
            "code", "code +/=", "redirect_uri", CALLBACK, "scope", "scope"), parameters);
    }

    @Test
    void sendsWechatTokenCredentialsWithProviderService() throws Exception {
        val api = new WechatApi20();
        val requests = new ArrayList<List<Object>>();
        val service = service(api, capturingTransport(requests));
        assertInstanceOf(WechatService.class, service);
        val config = configuration(api);
        val request = config.getTokenRequestBuilder().build(ctx, new OAuth20Credentials("code"), service);
        config.setHttpRequestConfigurer((outgoing, context) -> outgoing.addParameter("custom", "value"));
        config.getHttpRequestConfigurer().configure(request, ctx);
        assertThrows(IOException.class, () -> service.execute(request));

        val sent = requests.get(0);
        assertEquals(Verb.GET, sent.get(1));
        assertTrue(((String) sent.get(2)).startsWith(WechatApi20.TOKEN_ENDPOINT_URL + "?"));
        val parameters = queryParameters((String) sent.get(2));
        assertEquals(List.of("key"), parameters.get("appid"));
        assertEquals(List.of("secret"), parameters.get("secret"));
        assertEquals(List.of("code"), parameters.get("code"));
        assertEquals(List.of("key"), parameters.get("client_id"));
        assertEquals(List.of("secret"), parameters.get("client_secret"));
        assertEquals(List.of("value"), parameters.get("custom"));
    }

    @Test
    void sendsWechatProfileCredentialsWithCustomConfigurer() throws Exception {
        val api = new WechatApi20();
        val transport = mock(HttpClient.class);
        val response = new Response(200, "OK", Map.of(), "{\"openid\":\"user\",\"nickname\":\"name\"}");
        when(transport.execute(isNull(), anyMap(), any(), anyString(), any(byte[].class))).thenReturn(response);
        val service = service(api, transport);
        assertInstanceOf(WechatService.class, service);
        val config = new OAuth20Configuration() {
            @Override
            public OAuth20Service buildService(final CallContext context, final IndirectClient client) {
                init();
                return service;
            }
        };
        val client = new WechatClient("key", "secret");
        config.setKey("key");
        config.setSecret("secret");
        client.setConfiguration(config);
        client.setCallbackUrl(CALLBACK);
        client.init();
        config.setHttpRequestConfigurer((request, context) -> {
            assertSame(ctx, context);
            request.addParameter("custom", "value");
        });
        val creator = new WechatProfileCreator(config, client) {
            public Optional<UserProfile> retrieve(final WechatToken token) {
                return retrieveUserProfileFromToken(ctx, token);
            }
        };
        for (val scope : List.of("snsapi_base", "snsapi_userinfo")) {
            val token = new WechatToken("access-token", "bearer", 3600, null, scope, "{}", "user", "union");
            assertEquals("user", creator.retrieve(token).orElseThrow().getId());
        }
        val urls = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(transport, times(2)).execute(isNull(), anyMap(), eq(Verb.GET), urls.capture(), any(byte[].class));
        assertTrue(urls.getAllValues().get(0).startsWith("https://api.weixin.qq.com/sns/auth?"));
        assertTrue(urls.getAllValues().get(1).startsWith("https://api.weixin.qq.com/sns/userinfo?"));
        for (val url : urls.getAllValues()) {
            val parameters = queryParameters(url);
            assertEquals(List.of("key"), parameters.get("appid"));
            assertEquals(List.of("secret"), parameters.get("secret"));
            assertEquals(List.of("user"), parameters.get("openid"));
            assertEquals(List.of("access-token"), parameters.get("access_token"));
            assertEquals(List.of("value"), parameters.get("custom"));
        }
    }

    private Map<String, List<String>> queryParameters(final String url) {
        val parameters = new HashMap<String, List<String>>();
        for (val parameter : URI.create(url).getRawQuery().split("&")) {
            val pair = parameter.split("=", 2);
            val key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            val value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            parameters.computeIfAbsent(key, ignored -> new ArrayList<>()).add(value);
        }
        return parameters;
    }

    @Test
    void preservesOAuth1Signature() throws Exception {
        val timestamp = mock(TimestampService.class);
        when(timestamp.getTimestampInSeconds()).thenReturn("1234567890");
        when(timestamp.getNonce()).thenReturn("nonce");
        val api = new TwitterApi() {
            @Override
            public TimestampService getTimestampService() {
                return timestamp;
            }
        };
        val requests = new ArrayList<List<Object>>();
        val service = api.createService("key", "secret", CALLBACK, null, null, null, null, capturingTransport(requests));
        val token = new OAuth1RequestToken("request-token", "request-secret");
        assertThrows(IOException.class, () -> service.getAccessToken(token, "verifier"));
        val credentials = new OAuth10Credentials(token, token.getToken(), "verifier");
        val request = new OAuth10TokenRequestBuilder().build(ctx, credentials, service);
        service.signRequest(new OAuth1AccessToken(token.getToken(), token.getTokenSecret()), request);
        assertThrows(IOException.class, () -> service.execute(request));
        assertEquals(requests.get(0), requests.get(1));
    }

    @Test
    void configuresTokenAndProfileBeforeSerializationAndClosesTokenResponse() throws Exception {
        val api = new GenericApi20("https://provider.example/authorize", "https://provider.example/token");
        val transport = mock(HttpClient.class);
        val response = spy(new Response(200, "OK", Map.of(), "{\"access_token\":\"access-token\"}"));
        when(transport.execute(isNull(), anyMap(), any(), anyString(), any(byte[].class))).thenReturn(response);
        val service = service(api, transport);
        val config = new OAuth20Configuration() {
            @Override
            public OAuth20Service buildService(final CallContext context, final IndirectClient client) {
                init();
                return service;
            }
        };
        config.setApi(api);
        config.setKey("key");
        config.setSecret("secret");
        val definition = mock(OAuthProfileDefinition.class);
        config.setProfileDefinition(definition);
        when(definition.getProfileUrl(any(), same(config))).thenReturn(PROFILE);
        when(definition.getProfileVerb()).thenReturn(Verb.GET);
        when(definition.extractUserProfile(anyString())).thenReturn(new OAuth20Profile());
        val seen = new ArrayList<OAuthRequest>();
        config.setHttpRequestConfigurer((request, context) -> {
            assertSame(ctx, context);
            request.addParameter("custom", "value with spaces");
            if (request.getVerb() == Verb.GET) {
                assertEquals("Bearer access-token", request.getHeaders().get("Authorization"));
            }
            seen.add(request);
        });
        val client = new OAuth20Client();
        val credentials = new OAuth20Credentials("code");
        new OAuth20Authenticator(config, client).validate(ctx, credentials);
        verify(response).close();
        assertEquals("access-token", credentials.toAccessToken().getAccessToken());
        assertTrue(new OAuth20ProfileCreator(config, client).create(ctx, credentials).isPresent());
        assertEquals(2, seen.size());
        verify(transport).execute(isNull(), anyMap(), eq(Verb.POST), eq(api.getAccessTokenEndpoint()),
            argThat((byte[] body) -> new String(body, StandardCharsets.UTF_8).contains("custom=value%20with%20spaces")));
        verify(transport).execute(isNull(), anyMap(), eq(Verb.GET), eq(PROFILE + "?custom=value%20with%20spaces"), any(byte[].class));
    }

    @Test
    void preservesCustomBuilderOnInitialization() {
        for (val client : List.of(new CronofyClient(), new FigShareClient(), new WechatClient())) {
            client.setKey("key");
            client.setSecret("secret");
            client.setCallbackUrl(CALLBACK);
            val config = client.getConfiguration();
            val custom = new OAuth20TokenRequestBuilder();
            config.setTokenRequestBuilder(custom);
            client.init();
            config.init();
            client.reinit();
            config.reinit();
            assertSame(custom, config.getTokenRequestBuilder());
        }
    }

    private OAuth20Configuration configuration(final DefaultApi20 api) {
        val config = new OAuth20Configuration();
        config.setApi(api);
        config.setKey("key");
        config.setSecret("secret");
        config.setProfileDefinition(mock(OAuthProfileDefinition.class));
        config.init();
        return config;
    }

    private OAuth20Service service(final DefaultApi20 api, final HttpClient transport) {
        return api.createService("key", "secret", CALLBACK, "scope", "code", null, null, null, transport);
    }

    private HttpClient capturingTransport(final List<List<Object>> requests) throws Exception {
        val transport = mock(HttpClient.class);
        org.mockito.stubbing.Answer<Response> capture = invocation -> {
            val headers = new TreeMap<>((Map<String, String>) invocation.getArgument(1));
            val payload = invocation.getArgument(4);
            val body = payload instanceof byte[] bytes ? new String(bytes, StandardCharsets.UTF_8) : payload;
            requests.add(List.of(headers, invocation.getArgument(2), invocation.getArgument(3), body));
            throw new IOException("request captured");
        };
        when(transport.execute(isNull(), anyMap(), any(), anyString(), any(byte[].class))).thenAnswer(capture);
        when(transport.execute(isNull(), anyMap(), any(), anyString(), anyString())).thenAnswer(capture);
        return transport;
    }
}
