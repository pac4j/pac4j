package org.pac4j.oauth.profile.facebook;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a Facebook application.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookApplication implements Serializable {

    @Serial
    private static final long serialVersionUID = 8888597071833762957L;

    private String namespace;

    /**
     * <p>Getter for the field <code>namespace</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * <p>Setter for the field <code>namespace</code>.</p>
     *
     * @param namespace a {@link String} object
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
