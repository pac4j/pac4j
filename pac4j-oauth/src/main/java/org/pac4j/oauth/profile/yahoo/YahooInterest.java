package org.pac4j.oauth.profile.yahoo;

import java.util.List;

import org.pac4j.oauth.profile.JsonObject;

/**
 * This class represents a Yahoo interest.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooInterest extends JsonObject {
    
    private static final long serialVersionUID = 3613314161531695788L;
    
    private List<String> declaredInterests;
    
    private String interestCategory;

    public List<String> getDeclaredInterests() {
        return declaredInterests;
    }

    public void setDeclaredInterests(List<String> declaredInterests) {
        this.declaredInterests = declaredInterests;
    }

    public String getInterestCategory() {
        return interestCategory;
    }

    public void setInterestCategory(String interestCategory) {
        this.interestCategory = interestCategory;
    }
}
