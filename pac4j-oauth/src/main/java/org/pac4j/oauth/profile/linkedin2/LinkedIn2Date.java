package org.pac4j.oauth.profile.linkedin2;

import java.io.Serializable;

/**
 * This class represents a LinkedIn date.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2Date implements Serializable {
    
    private static final long serialVersionUID = 7741232980013691057L;
    
    private Integer year;
    
    private Integer month;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
}
