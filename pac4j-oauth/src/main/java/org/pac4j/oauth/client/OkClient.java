/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.ok.OkAttributesDefinition;
import org.pac4j.oauth.profile.ok.OkProfile;
import org.scribe.builder.api.OkApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

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
    protected String getProfileUrl(Token accessToken) {
        String baseParams =
                "application_key=" + publicKey +
                        "&format=json" +
                        "&method=users.getCurrentUser";
        String finalSign;
        try {
            String preSign = getMD5SignAsHexString(accessToken.getToken() + secret);
            finalSign = getMD5SignAsHexString(baseParams.replaceAll("&", "") + preSign);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        return API_BASE_URL + baseParams + "&access_token=" + accessToken.getToken() + "&sig=" + finalSign;
    }

    @Override
    protected OkProfile extractUserProfile(String body) {
        final OkProfile profile = new OkProfile();
        JsonNode userNode = JsonHelper.getFirstNode(body);
        if (userNode != null) {
            profile.setId(JsonHelper.get(userNode, OkAttributesDefinition.UID));
            for (final String attribute : OAuthAttributesDefinitions.okDefinition.getAllAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(userNode, attribute));
            }
        }
        return profile;
    }

    private String getMD5SignAsHexString(String strForEncoding) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        StringBuilder result = new StringBuilder();
        for (byte aByte : md.digest(strForEncoding.getBytes("UTF-8"))) {
            if ((0xff & aByte) < 0x10) {
                result.append("0").append(Integer.toHexString((0xFF & aByte)));
            } else {
                result.append(Integer.toHexString(0xFF & aByte));
            }
        }
        return result.toString();
    }

    @Override
    protected void internalInit(final WebContext context) {
        super.internalInit(context);
        CommonHelper.assertNotBlank("publicKey", this.publicKey);
        this.service = new ProxyOAuth20ServiceImpl(new OkApi(),
                new OAuthConfig(this.key, this.secret, computeFinalCallbackUrl(context), SignatureType.Header, null, null), this.connectTimeout, this.readTimeout,
                this.proxyHost, this.proxyPort, false, true);
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
