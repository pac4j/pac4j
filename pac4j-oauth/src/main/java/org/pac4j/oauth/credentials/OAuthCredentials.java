package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.Token;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents an OAuth credentials for OAuth 1.0 &amp; 2.0 : a request token, a token and a verifier.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class OAuthCredentials extends Credentials {
    
    private static final long serialVersionUID = -7705033802712382951L;
    
    private Token requestToken;
    
    private String token;
    
    private String verifier;
    
    public OAuthCredentials(final String verifier, final String clientName) {
        this.requestToken = null;
        this.token = null;
        this.verifier = verifier;
        setClientName(clientName);
    }
    
    public OAuthCredentials(final Token requestToken, final String token, final String verifier, final String clientName) {
        this.requestToken = requestToken;
        this.token = token;
        this.verifier = verifier;
        setClientName(clientName);
    }
    
    public Token getRequestToken() {
        return this.requestToken;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public String getVerifier() {
        return this.verifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final OAuthCredentials that = (OAuthCredentials) o;

        if (requestToken != null ? !requestToken.equals(that.requestToken) : that.requestToken != null) return false;
        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        return !(verifier != null ? !verifier.equals(that.verifier) : that.verifier != null);

    }

    @Override
    public int hashCode() {
        int result = requestToken != null ? requestToken.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (verifier != null ? verifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "requestToken", this.requestToken, "token", this.token,
                                     "verifier", this.verifier, "clientName", getClientName());
    }
}
