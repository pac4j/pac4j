package org.pac4j.oauth.profile.yahoo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import static org.pac4j.core.util.CommonHelper.newDate;

/**
 * This class represents a Yahoo disclosure.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooDisclosure implements Serializable {

    private static final long serialVersionUID = 1592628531426071633L;

    private String acceptance;

    private String name;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date seen;

    private String version;

    public String getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(String acceptance) {
        this.acceptance = acceptance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getSeen() {
        return newDate(seen);
    }

    public void setSeen(Date seen) {
        this.seen = newDate(seen);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
