package org.pac4j.oauth.profile.wordpress;

import java.io.Serializable;

/**
 * This class represents the links in WordPress.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class WordPressLinks implements Serializable {

    private static final long serialVersionUID = 650184033370922722L;

    private String self;

    private String help;

    private String site;

    public String getSelf() {
        return self;
    }

    public void setSelf(final String self) {
        this.self = self;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(final String help) {
        this.help = help;
    }

    public String getSite() {
        return site;
    }

    public void setSite(final String site) {
        this.site = site;
    }
}
