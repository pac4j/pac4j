package org.pac4j.oauth.profile.facebook;

import java.io.Serializable;

/**
 * This class represents a Facebook application.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookApplication implements Serializable {
    
    private static final long serialVersionUID = 8888597071833762957L;
    
    private String namespace;
    
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }
}
