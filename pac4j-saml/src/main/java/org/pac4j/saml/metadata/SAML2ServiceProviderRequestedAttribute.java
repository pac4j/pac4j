package org.pac4j.saml.metadata;

import java.io.Serializable;

/**
 * This is {@link SAML2ServiceProviderRequestedAttribute}.
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

    public SAML2ServiceProviderRequestedAttribute() {
    }

    public SAML2ServiceProviderRequestedAttribute(final String name, final String friendlyName) {
        this.name = name;
        this.friendlyName = friendlyName;
    }

    public SAML2ServiceProviderRequestedAttribute(final String name, final String friendlyName,
                                                  final String nameFormat, final boolean isRequired) {
        this.name = name;
        this.friendlyName = friendlyName;
        this.nameFormat = nameFormat;
        this.isRequired = isRequired;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceLang() {
        return serviceLang;
    }

    public void setServiceLang(final String serviceLang) {
        this.serviceLang = serviceLang;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(final String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getNameFormat() {
        return nameFormat;
    }

    public void setNameFormat(final String nameFormat) {
        this.nameFormat = nameFormat;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(final boolean required) {
        isRequired = required;
    }

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
