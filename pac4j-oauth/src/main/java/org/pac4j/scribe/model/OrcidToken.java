package org.pac4j.scribe.model;

import com.github.scribejava.core.model.OAuth2AccessToken;

/**
 * This class represents a specific Token for ORCiD using OAuth protocol version 2. It could be part of the Scribe library.
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidToken extends OAuth2AccessToken {

    private static final long serialVersionUID = 3129683748679852572L;
    private String orcid;

    public OrcidToken(String accessToken, String tokenType, Integer expiresIn, String refreshToken, String scope, String orcid, 
        String response) {
        super(accessToken, tokenType, expiresIn, refreshToken, scope, response);
        setOrcid(orcid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OrcidToken that = (OrcidToken) o;

        return !(orcid != null ? !orcid.equals(that.orcid) : that.orcid != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (orcid != null ? orcid.hashCode() : 0);
        return result;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
}
