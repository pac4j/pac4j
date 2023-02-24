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

    /**
     * <p>Getter for the field <code>acceptance</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getAcceptance() {
        return acceptance;
    }

    /**
     * <p>Setter for the field <code>acceptance</code>.</p>
     *
     * @param acceptance a {@link java.lang.String} object
     */
    public void setAcceptance(String acceptance) {
        this.acceptance = acceptance;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>seen</code>.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getSeen() {
        return newDate(seen);
    }

    /**
     * <p>Setter for the field <code>seen</code>.</p>
     *
     * @param seen a {@link java.util.Date} object
     */
    public void setSeen(Date seen) {
        this.seen = newDate(seen);
    }

    /**
     * <p>Getter for the field <code>version</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getVersion() {
        return version;
    }

    /**
     * <p>Setter for the field <code>version</code>.</p>
     *
     * @param version a {@link java.lang.String} object
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
