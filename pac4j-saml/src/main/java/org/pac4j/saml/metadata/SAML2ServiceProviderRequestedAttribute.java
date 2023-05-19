package org.pac4j.saml.metadata;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * This is {@link SAML2ServiceProviderRequestedAttribute}.
 *
 * @author Misagh Moayyed
 */
@Getter
@Setter
@ToString
public class SAML2ServiceProviderRequestedAttribute implements Serializable {
    @Serial
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
     * @param name a {@link String} object
     * @param friendlyName a {@link String} object
     */
    public SAML2ServiceProviderRequestedAttribute(final String name, final String friendlyName) {
        this.name = name;
        this.friendlyName = friendlyName;
    }

    /**
     * <p>Constructor for SAML2ServiceProviderRequestedAttribute.</p>
     *
     * @param name a {@link String} object
     * @param friendlyName a {@link String} object
     * @param nameFormat a {@link String} object
     * @param isRequired a boolean
     */
    public SAML2ServiceProviderRequestedAttribute(final String name, final String friendlyName,
                                                  final String nameFormat, final boolean isRequired) {
        this.name = name;
        this.friendlyName = friendlyName;
        this.nameFormat = nameFormat;
        this.isRequired = isRequired;
    }
}
