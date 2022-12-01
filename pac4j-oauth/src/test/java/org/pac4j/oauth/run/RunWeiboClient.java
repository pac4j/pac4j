package org.pac4j.oauth.run;

import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.WeiboClient;
import org.pac4j.oauth.profile.weibo.WeiboProfile;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Run manually a test for the {@link WeiboClient}.
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public final class RunWeiboClient extends RunClient {

    public static void main(String[] args) throws Exception {
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("https.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "8888");
//        System.setProperty("https.proxyPort", "8888");
//        System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\Administrator\\cacerts");
//        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
//        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        new RunWeiboClient().run();
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
        val apiKey = "3722350620";
        val apiSecret = "3edbe998b0d53130db83928c330c879b";

        val weiboClient = new WeiboClient();
        weiboClient.setKey(apiKey);
        weiboClient.setSecret(apiSecret);
        weiboClient.setCallbackUrl("https://git.xjiakao.com/cas/login?client_name=WeiboClient");
        weiboClient.setScope(WeiboClient.WeiboScope.EMAIL);
        return weiboClient;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        val profile = (WeiboProfile) userProfile;
        assertEquals("6591860688", profile.getId());
        assertEquals(WeiboProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "6591860688",
            profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), WeiboProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, "OAuth2", null, "OAuth2", "OAuth2",
            Gender.MALE, Locale.SIMPLIFIED_CHINESE,
            "http://tvax4.sinaimg.cn/crop.7.6.386.386.1024/007c6M5Gly8fsyu16gbbhj30b40b4gm7.jpg",
            "http://weibo.com/u/6591860688", "海外");
        assertEquals(Locale.SIMPLIFIED_CHINESE, profile.getLocale());
        assertEquals(34, profile.getAttributes().size());
    }
}
