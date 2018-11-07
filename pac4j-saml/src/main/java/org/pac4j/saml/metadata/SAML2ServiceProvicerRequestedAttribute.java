package org.pac4j.saml.metadata;

import java.io.Serializable;

/**
 * This is {@link SAML2ServiceProvicerRequestedAttribute}.
 *
 * @author Misagh Moayyed
 */
public class SAML2ServiceProvicerRequestedAttribute implements Serializable {
    private static final long serialVersionUID = 1040516205957826527L;

    public String name;
    public String friendlyName;
    public String nameFormat = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";
    public boolean isRequired;

    public SAML2ServiceProvicerRequestedAttribute() {
    }

    public SAML2ServiceProvicerRequestedAttribute(final String name, final String friendlyName) {
        this.name = name;
        this.friendlyName = friendlyName;
    }

    public SAML2ServiceProvicerRequestedAttribute(final String name, final String friendlyName,
                                                  final String nameFormat, final boolean isRequired) {
        this.name = name;
        this.friendlyName = friendlyName;
        this.nameFormat = nameFormat;
        this.isRequired = isRequired;
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
        return "RequestedServiceProviderAttribute{" +
            "name='" + name + '\'' +
            ", friendlyName='" + friendlyName + '\'' +
            ", nameFormat='" + nameFormat + '\'' +
            ", isRequired=" + isRequired +
            '}';
    }
}
