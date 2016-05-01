package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.apis.OdnoklassnikiApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.ok.OkAttributesDefinition;
import org.pac4j.oauth.profile.ok.OkProfile;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public final class OkClient extends BaseOAuth20Client<OkProfile> {

    private static final String API_BASE_URL = "http://api.ok.ru/fb.do?";

    /**
     * Public key (required as well as application key by API on ok.ru)
     */
    private String publicKey;

    public OkClient() {
    }

    public OkClient(final String key, final String secret,final String publicKey) {
        setKey(key);
        setSecret(secret);
        setPublicKey(publicKey);
    }


    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("publicKey", this.publicKey);
        super.internalInit(context);
    }

    @Override
    protected BaseApi<OAuth20Service> getApi() {
        return OdnoklassnikiApi.instance();
    }

    @Override
    protected  boolean hasOAuthGrantType() {
        return true;
    }

    @Override
    protected String getProfileUrl(OAuth2AccessToken accessToken) {
        String baseParams =
                "application_key=" + publicKey +
                        "&format=json" +
                        "&method=users.getCurrentUser";
        String finalSign;
        try {
            String preSign = getMD5SignAsHexString(accessToken.getAccessToken() + getSecret());
            finalSign = getMD5SignAsHexString(baseParams.replaceAll("&", "") + preSign);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        return API_BASE_URL + baseParams + "&access_token=" + accessToken.getAccessToken() + "&sig=" + finalSign;
    }

    @Override
    protected OkProfile extractUserProfile(String body) throws HttpAction {
        final OkProfile profile = new OkProfile();
        JsonNode userNode = JsonHelper.getFirstNode(body);
        if (userNode != null) {
            profile.setId(JsonHelper.getElement(userNode, OkAttributesDefinition.UID));
            for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
                profile.addAttribute(attribute, JsonHelper.getElement(userNode, attribute));
            }
        }
        return profile;
    }

    private String getMD5SignAsHexString(String strForEncoding) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        StringBuilder result = new StringBuilder();
        for (byte aByte : md.digest(strForEncoding.getBytes("UTF-8"))) {
            if ((0xff & aByte) < 0x10) {
                result.append("0").append(Integer.toHexString(0xFF & aByte));
            } else {
                result.append(Integer.toHexString(0xFF & aByte));
            }
        }
        return result.toString();
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
