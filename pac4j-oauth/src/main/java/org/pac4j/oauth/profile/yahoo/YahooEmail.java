package org.pac4j.oauth.profile.yahoo;

import org.pac4j.oauth.profile.JsonObject;

/**
 * This class represents a Yahoo email.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooEmail extends JsonObject {
    
    private static final long serialVersionUID = 1195905995057732685L;
    
    private Integer id;
    
    private Boolean primary;
    
    private String handle;
    
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
