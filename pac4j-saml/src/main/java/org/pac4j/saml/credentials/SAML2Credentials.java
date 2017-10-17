package org.pac4j.saml.credentials;

import java.util.List;

import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.core.credentials.Credentials;

/**
 * Credentials containing the nameId of the SAML subject and all of its attributes.
 *
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAML2Credentials extends Credentials {

    private static final long serialVersionUID = 5040516205957826527L;

    private NameID nameId;

    private String sessionIndex;

    private List<Attribute> attributes;

    private Conditions conditions;

    private String issuerId;

    private List<String> authnContexts;

    public SAML2Credentials(final NameID nameId, final String issuerId, final List<Attribute> attributes, final Conditions conditions,
                            final String sessionIndex, final List<String> authnContexts) {
        this.nameId = nameId;
        this.issuerId = issuerId;
        this.sessionIndex = sessionIndex;
        this.attributes = attributes;
        this.conditions = conditions;
        this.authnContexts = authnContexts;
    }

    public final NameID getNameId() {
        return this.nameId;
    }

    public final String getSessionIndex() {
        return this.sessionIndex;
    }

    public final List<Attribute> getAttributes() {
        return this.attributes;
    }

    public Conditions getConditions() {
        return this.conditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SAML2Credentials that = (SAML2Credentials) o;

        if (nameId != null ? !nameId.equals(that.nameId) : that.nameId != null) return false;
        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) return false;
        if (sessionIndex != null ? sessionIndex.equals(that.sessionIndex) : that.sessionIndex != null) return false;
        return !(conditions != null ? !conditions.equals(that.conditions) : that.conditions != null);

    }

    @Override
    public int hashCode() {
        int result = nameId != null ? nameId.hashCode() : 0;
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (sessionIndex != null ? sessionIndex.hashCode() : 0);
        result = 31 * result + (conditions != null ? conditions.hashCode() : 0);
        return result;
    }

    @Override
    public final String toString() {
        return "SAMLCredential [nameId=" + this.nameId + ", attributes=" + this.attributes + ", sessionIndex=" + this.sessionIndex + "]";
    }

    public String getIssuerId() {
        return issuerId;
    }

    public List<String> getAuthnContexts() {
        return authnContexts;
    }
}
