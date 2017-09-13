package org.pac4j.oauth.profile.facebook;

import java.io.Serializable;

/**
 * This class represents a Facebook group.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookGroup implements Serializable {

    private static final long serialVersionUID = -846266834053161809L;

    private String id;

    private String name;

    private String privacy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }
}
