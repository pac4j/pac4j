package org.pac4j.oauth.profile.wordpress;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents the links in WordPress.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class WordPressLinks implements Serializable {

    @Serial
    private static final long serialVersionUID = 650184033370922722L;

    private String self;

    private String help;

    private String site;

    /**
     * <p>Getter for the field <code>self</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getSelf() {
        return self;
    }

    /**
     * <p>Setter for the field <code>self</code>.</p>
     *
     * @param self a {@link String} object
     */
    public void setSelf(String self) {
        this.self = self;
    }

    /**
     * <p>Getter for the field <code>help</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getHelp() {
        return help;
    }

    /**
     * <p>Setter for the field <code>help</code>.</p>
     *
     * @param help a {@link String} object
     */
    public void setHelp(String help) {
        this.help = help;
    }

    /**
     * <p>Getter for the field <code>site</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getSite() {
        return site;
    }

    /**
     * <p>Setter for the field <code>site</code>.</p>
     *
     * @param site a {@link String} object
     */
    public void setSite(String site) {
        this.site = site;
    }
}
