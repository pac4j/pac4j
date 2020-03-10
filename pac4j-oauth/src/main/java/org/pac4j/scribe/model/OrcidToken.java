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

    public OrcidToken(final String accessToken, final String tokenType, final Integer expiresIn,
                      final String refreshToken, final String scope, final String orcid,
                      final String response) {
        super(accessToken, tokenType, expiresIn, refreshToken, scope, response);
        setOrcid(orcid);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final OrcidToken that = (OrcidToken) o;

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

    public void setOrcid(final String orcid) {
        this.orcid = orcid;
    }
}
