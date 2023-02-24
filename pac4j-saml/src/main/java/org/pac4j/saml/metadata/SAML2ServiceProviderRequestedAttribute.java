package org.pac4j.saml.metadata;

import java.io.Serializable;

/**
 * This is {@link org.pac4j.saml.metadata.SAML2ServiceProviderRequestedAttribute}.
 *
 * @author Misagh Moayyed
 */
public class SAML2ServiceProviderRequestedAttribute implements Serializable {
    private static final long serialVersionUID = 1040516205957826527L;

    public String name;
    public String friendlyName;
    public String nameFormat = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";
    public boolean isRequired;
    private String serviceName;
    private String serviceLang;

    /**
     * <p>Constructor for SAML2ServiceProviderRequestedAttribute.</p>
     */
    public SAML2ServiceProviderRequestedAttribute() {
    }

    /**
     * <p>Constructor for SAML2ServiceProviderRequestedAttribute.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param friendlyName a {@link java.lang.String} object
     */
    public SAML2ServiceProviderRequestedAttribute(final String name, final String friendlyName) {
        this.name = name;
        this.friendlyName = friendlyName;
    }

    /**
     * <p>Constructor for SAML2ServiceProviderRequestedAttribute.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param friendlyName a {@link java.lang.String} object
     * @param nameFormat a {@link java.lang.String} object
     * @param isRequired a boolean
     */
    public SAML2ServiceProviderRequestedAttribute(final String name, final String friendlyName,
                                                  final String nameFormat, final boolean isRequired) {
        this.name = name;
        this.friendlyName = friendlyName;
        this.nameFormat = nameFormat;
        this.isRequired = isRequired;
    }

    /**
     * <p>Getter for the field <code>serviceName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * <p>Setter for the field <code>serviceName</code>.</p>
     *
     * @param serviceName a {@link java.lang.String} object
     */
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * <p>Getter for the field <code>serviceLang</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getServiceLang() {
        return serviceLang;
    }

    /**
     * <p>Setter for the field <code>serviceLang</code>.</p>
     *
     * @param serviceLang a {@link java.lang.String} object
     */
    public void setServiceLang(final String serviceLang) {
        this.serviceLang = serviceLang;
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
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>friendlyName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * <p>Setter for the field <code>friendlyName</code>.</p>
     *
     * @param friendlyName a {@link java.lang.String} object
     */
    public void setFriendlyName(final String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * <p>Getter for the field <code>nameFormat</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getNameFormat() {
        return nameFormat;
    }

    /**
     * <p>Setter for the field <code>nameFormat</code>.</p>
     *
     * @param nameFormat a {@link java.lang.String} object
     */
    public void setNameFormat(final String nameFormat) {
        this.nameFormat = nameFormat;
    }

    /**
     * <p>isRequired.</p>
     *
     * @return a boolean
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * <p>setRequired.</p>
     *
     * @param required a boolean
     */
    public void setRequired(final boolean required) {
        isRequired = required;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "SAML2ServiceProviderRequestedAttribute{" +
            "name='" + name + '\'' +
            ", friendlyName='" + friendlyName + '\'' +
            ", nameFormat='" + nameFormat + '\'' +
            ", isRequired=" + isRequired +
            ", serviceName='" + serviceName + '\'' +
            ", serviceLang='" + serviceLang + '\'' +
            '}';
    }
}
