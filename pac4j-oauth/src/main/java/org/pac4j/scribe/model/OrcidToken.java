package org.pac4j.scribe.model;

import com.github.scribejava.core.model.Token;

/**
 * This class represents a specific Token for ORCiD using OAuth protocol version 2. It could be part of the Scribe library.
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidToken extends Token {

    private String orcid;

    public OrcidToken(String token, String secret, String orcid, String rawResponse) {
        super(token, secret, rawResponse);
        setOrcid(orcid);
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
}
