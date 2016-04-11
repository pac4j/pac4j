package org.pac4j.oauth.profile.google2;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pac4j.oauth.profile.JsonObject;

/**
 * This class represents an email object for Google.
 *
 * @author Nate Williams
 * @since 1.6.1
 */
public final class Google2Email extends JsonObject {

    private static final long serialVersionUID = 3273984944635729083L;

    @JsonProperty("value")
    private String email;
    private String type;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
