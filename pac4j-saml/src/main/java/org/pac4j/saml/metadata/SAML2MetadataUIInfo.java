package org.pac4j.saml.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * This is {@link SAML2MetadataUIInfo} that allows one to specify
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

    public List<SAML2MetadataUILogo> getLogos() {
        return logos;
    }

    public void setLogos(final List<SAML2MetadataUILogo> logos) {
        this.logos = logos;
    }

    public List<String> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final List<String> displayNames) {
        this.displayNames = displayNames;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(final List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(final List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getInformationUrls() {
        return informationUrls;
    }

    public void setInformationUrls(final List<String> informationUrls) {
        this.informationUrls = informationUrls;
    }

    public List<String> getPrivacyUrls() {
        return privacyUrls;
    }

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
