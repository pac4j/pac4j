package org.pac4j.oauth.profile.google2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * This class represents an email object for Google.
 *
 * @author Nate Williams
 * @since 1.6.1
 */
public final class Google2Email implements Serializable {

    private static final long serialVersionUID = 3273984944635729083L;

    @JsonProperty("value")
    private String email;
    private String type;

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
