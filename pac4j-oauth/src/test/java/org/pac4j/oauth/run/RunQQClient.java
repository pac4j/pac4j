package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.QQClient;
import org.pac4j.oauth.profile.qq.QQProfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Run manually a test for the {@link QQClient}.
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public final class RunQQClient extends RunClient {

    public static void main(String[] args) throws Exception {
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("https.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "8888");
//        System.setProperty("https.proxyPort", "8888");
//        System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\Administrator\\cacerts");
//        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
//        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        final RunQQClient runQQClient = new RunQQClient();
        runQQClient.run();
    }

    @Override
    protected String getLogin() {
        return "pac4j@sina.com";
    }

    @Override
    protected String getPassword() {
        return "github.com";
    }

    @Override
    protected IndirectClient getClient() {
        final QQClient qqClient = new QQClient();
        final String apiKey = "101481951";
        final String apiSecret = "60e9b925304a223403c7342efffcfa76";

        qqClient.setKey(apiKey);
        qqClient.setSecret(apiSecret);
        qqClient.setCallbackUrl("https://mtsapi.house365.com/callback");
        qqClient.addScope(QQClient.QQScope.GET_USER_INFO);
        return qqClient;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final QQProfile profile = (QQProfile) userProfile;
        assertEquals("8585AB16822E1437050D63C27D277991", profile.getId());
        assertEquals(QQProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "8585AB16822E1437050D63C27D277991",
            profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), QQProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, null, null, "PAC4J", "PAC4J",
            Gender.MALE, null,
            "http://thirdqq.qlogo.cn/qqapp/101481951/8585AB16822E1437050D63C27D277991/100",
            null, "江苏 南京");
        assertEquals(13, profile.getAttributes().size());
    }
}
