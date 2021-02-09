package org.pac4j.scribe.extractors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.scribe.model.WeiboToken;

import com.github.scribejava.core.exceptions.OAuthException;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Weibo token extra test.
 * <p>More info at: <a href="http://open.weibo.com/wiki/Oauth2/access_token">Oauth2/access token</a></p>
 * <p>
 */
public class WeiboJsonExtractorTest {

    private final WeiboJsonExtractor extractor = WeiboJsonExtractor.instance();

    private final ObjectMapper mapper = new ObjectMapper();

    private String responseOk = " {\n" +
        "       \"access_token\": \"ACCESS_TOKEN\",\n" +
        "       \"expires_in\": 1234,\n" +
        "       \"remind_in\":\"798114\",\n" +
        "       \"uid\":\"12341234\"\n" +
        " }";
    private String responseError = " {\n" +
        "       \"access_token\": \"ACCESS_TOKEN\",\n" +
        "       \"expires_in\": 1234,\n" +
        "       \"remind_in\":\"798114\"\n" +
        " }";

    @Test
    public void createTokenHasUid() throws IOException {
        var accessToken = extractor.createToken("ACCESS_TOKEN", null,
            123, null, null, mapper.readTree(responseOk), responseOk);
        Assert.assertEquals("ACCESS_TOKEN", accessToken.getAccessToken());
        assertTrue(accessToken instanceof WeiboToken);
        if (accessToken instanceof WeiboToken) {
            Assert.assertEquals("12341234", ((WeiboToken) accessToken).getUid());
        }
    }

    @Test(expected = OAuthException.class)
    public void createTokenWithOutUid() throws IOException {
        extractor.createToken("ACCESS_TOKEN", null,
            123, null, null, mapper.readTree(responseError), responseError);
    }
}
