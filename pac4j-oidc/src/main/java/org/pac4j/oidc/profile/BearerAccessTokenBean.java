package org.pac4j.oidc.profile;

import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;

import java.io.Serializable;
import java.util.List;

/**
 * Serializable access token.
 *
 * @author Michael Remond
 * @version 1.7.0
 */
public class BearerAccessTokenBean implements Serializable {

    private static final long serialVersionUID = 7726472295622796149L;

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
        final Scope scope = token.getScope();
        if (scope != null) {
            return new BearerAccessTokenBean(token.getValue(),
                    token.getLifetime(), scope.toStringList());
        } else {
            return new BearerAccessTokenBean(token.getValue(),
                    token.getLifetime(), null);
        }
    }

    public static BearerAccessToken fromBean(final BearerAccessTokenBean token) {
        return new BearerAccessToken(token.value, token.lifetime,
                Scope.parse(token.scope));
    }
}
