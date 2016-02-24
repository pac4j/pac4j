package org.pac4j.oauth.profile.linkedin2;

import org.pac4j.oauth.profile.JsonObject;

/**
 * This class represents a LinkedIn location.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2Location extends JsonObject {
    
    private static final long serialVersionUID = -7548166141136051112L;
    
    private String name;
    
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
