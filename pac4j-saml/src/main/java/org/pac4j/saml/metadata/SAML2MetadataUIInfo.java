package org.pac4j.saml.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * This is {@link org.pac4j.saml.metadata.SAML2MetadataUIInfo} that allows one to specify
 * metadata UI information in saml2 metadata generation.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class SAML2MetadataUIInfo {
    private List<String> displayNames = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();
    private List<String> keywords = new ArrayList<>();
    private List<String> informationUrls = new ArrayList<>();
    private List<String> privacyUrls = new ArrayList<>();
    private List<SAML2MetadataUILogo> logos = new ArrayList<>();

    /**
     * <p>Getter for the field <code>logos</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<SAML2MetadataUILogo> getLogos() {
        return logos;
    }

    /**
     * <p>Setter for the field <code>logos</code>.</p>
     *
     * @param logos a {@link java.util.List} object
     */
    public void setLogos(final List<SAML2MetadataUILogo> logos) {
        this.logos = logos;
    }

    /**
     * <p>Getter for the field <code>displayNames</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getDisplayNames() {
        return displayNames;
    }

    /**
     * <p>Setter for the field <code>displayNames</code>.</p>
     *
     * @param displayNames a {@link java.util.List} object
     */
    public void setDisplayNames(final List<String> displayNames) {
        this.displayNames = displayNames;
    }

    /**
     * <p>Getter for the field <code>descriptions</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getDescriptions() {
        return descriptions;
    }

    /**
     * <p>Setter for the field <code>descriptions</code>.</p>
     *
     * @param descriptions a {@link java.util.List} object
     */
    public void setDescriptions(final List<String> descriptions) {
        this.descriptions = descriptions;
    }

    /**
     * <p>Getter for the field <code>keywords</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * <p>Setter for the field <code>keywords</code>.</p>
     *
     * @param keywords a {@link java.util.List} object
     */
    public void setKeywords(final List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * <p>Getter for the field <code>informationUrls</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getInformationUrls() {
        return informationUrls;
    }

    /**
     * <p>Setter for the field <code>informationUrls</code>.</p>
     *
     * @param informationUrls a {@link java.util.List} object
     */
    public void setInformationUrls(final List<String> informationUrls) {
        this.informationUrls = informationUrls;
    }

    /**
     * <p>Getter for the field <code>privacyUrls</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getPrivacyUrls() {
        return privacyUrls;
    }

    /**
     * <p>Setter for the field <code>privacyUrls</code>.</p>
     *
     * @param privacyUrls a {@link java.util.List} object
     */
    public void setPrivacyUrls(final List<String> privacyUrls) {
        this.privacyUrls = privacyUrls;
    }

    public static class SAML2MetadataUILogo {
        private String url;
        private int width;
        private int height;

        public SAML2MetadataUILogo(final String url, final int width, final int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(final int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(final int height) {
            this.height = height;
        }
    }
}
