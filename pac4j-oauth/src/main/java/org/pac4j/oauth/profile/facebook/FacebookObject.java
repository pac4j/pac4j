package org.pac4j.oauth.profile.facebook;

import org.pac4j.oauth.profile.JsonObject;

/**
 * This class represents a common Facebook object (id + name).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class FacebookObject extends JsonObject {
    
    private static final long serialVersionUID = 7393867411970930893L;
    
    private String id;
    
    private String name;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
}
