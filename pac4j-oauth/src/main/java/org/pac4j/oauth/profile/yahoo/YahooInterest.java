package org.pac4j.oauth.profile.yahoo;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents a Yahoo interest.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooInterest implements Serializable {

    private static final long serialVersionUID = 3613314161531695788L;

    private List<String> declaredInterests;

    private String interestCategory;

    /**
     * <p>Getter for the field <code>declaredInterests</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getDeclaredInterests() {
        return declaredInterests;
    }

    /**
     * <p>Setter for the field <code>declaredInterests</code>.</p>
     *
     * @param declaredInterests a {@link java.util.List} object
     */
    public void setDeclaredInterests(List<String> declaredInterests) {
        this.declaredInterests = declaredInterests;
    }

    /**
     * <p>Getter for the field <code>interestCategory</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getInterestCategory() {
        return interestCategory;
    }

    /**
     * <p>Setter for the field <code>interestCategory</code>.</p>
     *
     * @param interestCategory a {@link java.lang.String} object
     */
    public void setInterestCategory(String interestCategory) {
        this.interestCategory = interestCategory;
    }
}
