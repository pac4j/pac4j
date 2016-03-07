package org.pac4j.oauth.profile.linkedin2;

import java.io.Serializable;

/**
 * This class represents a LinkedIn company.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2Company implements Serializable {
    
    private static final long serialVersionUID = -2516111031032736648L;
    
    private String name;
    
    private String industry;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
