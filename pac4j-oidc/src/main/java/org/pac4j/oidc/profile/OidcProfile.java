/*
  Copyright 2012 -2014 pac4j organization

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

package org.pac4j.oidc.profile;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.oidc.client.OidcClient;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

/**
 * <p>This class is the user profile for sites using OpenID Connect protocol.</p>
 * <p>It is returned by the {@link OidcClient}.</p>
 * 
 * @author Michael Remond
 * @version 1.7.0
 */
public class OidcProfile extends CommonProfile implements Externalizable {

    private static final long serialVersionUID = -52855988661742374L;

    private BearerAccessToken accessToken;
    private String idTokenString;
    private JWT idToken;

    public OidcProfile() {
    }

    public OidcProfile(BearerAccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public BearerAccessToken getAccessToken() {
        return this.accessToken;
    }

    public String getIdTokenString() {
        return idTokenString;
    }

    public void setIdTokenString(String idTokenString) {
        this.idTokenString = idTokenString;
    }

    public JWT getIdToken() throws ParseException {
        if (this.idToken == null && this.idTokenString != null) {
            this.idToken = JWTParser.parse(this.idTokenString);
        }

        return this.idToken;
    }


    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        super.writeExternal(out);
        final BearerAccessTokenBean bean = BearerAccessTokenBean.toBean(this.accessToken);
        out.writeObject(bean);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        final BearerAccessTokenBean bean = (BearerAccessTokenBean) in.readObject();
        this.accessToken = BearerAccessTokenBean.fromBean(bean);
    }

    private static class BearerAccessTokenBean implements Serializable {
        private String value;
        private long lifetime;
        private List<String> scope;

        public BearerAccessTokenBean(final String value, final long lifetime,
                                     final List<String> scope) {
            this.value = value;
            this.lifetime = lifetime;
            this.scope = scope;
        }

        public static BearerAccessTokenBean toBean(final BearerAccessToken token) {
            return new BearerAccessTokenBean(token.getValue(),
                    token.getLifetime(), token.getScope().toStringList());
        }

        public static BearerAccessToken fromBean(final BearerAccessTokenBean token) {
            return new BearerAccessToken(token.value, token.lifetime,
                    Scope.parse(token.scope));
        }
    }
}
