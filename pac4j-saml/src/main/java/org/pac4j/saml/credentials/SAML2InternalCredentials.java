package org.pac4j.saml.credentials;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.core.credentials.AuthenticationCredentials;
import org.pac4j.core.profile.converter.AttributeConverter;

import java.io.Serializable;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Credentials containing the nameId of the SAML subject and all of its attributes.
 *
 * @author Michael Remond
 * @since 1.5.0
 */
@Slf4j
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class SAML2InternalCredentials extends AuthenticationCredentials {

    private static final long serialVersionUID = 5040516205957826527L;

    private final SAMLNameID nameId;

    private final String sessionIndex;

    private final List<SAMLAttribute> attributes;

    private final SAMLConditions conditions;

    private final String issuerId;

    private final List<String> authnContexts;
    private final List<String> authnContextAuthorities;

    private final String inResponseTo;

    public SAML2InternalCredentials(final SAMLNameID nameId, final String issuerId,
                                    final List<SAMLAttribute> samlAttributes, final Conditions conditions,
                                    final String sessionIndex, final List<String> authnContexts,
                                    final List<String> authnContextAuthorities,
                                    final String inResponseTo) {
        this.nameId = nameId;
        this.issuerId = issuerId;
        this.sessionIndex = sessionIndex;
        this.attributes = samlAttributes;
        this.inResponseTo = inResponseTo;

        if (conditions != null) {
            this.conditions = new SAMLConditions();

            if (conditions.getNotBefore() != null) {
                this.conditions.setNotBefore(ZonedDateTime.ofInstant(conditions.getNotBefore(), ZoneOffset.UTC));
            }

            if (conditions.getNotOnOrAfter() != null) {
                this.conditions.setNotOnOrAfter(ZonedDateTime.ofInstant(conditions.getNotOnOrAfter(), ZoneOffset.UTC));
            }
        } else {
            this.conditions = null;
        }
        this.authnContextAuthorities = authnContextAuthorities;
        this.authnContexts = authnContexts;

        LOGGER.info("Constructed SAML2 credentials: {}", this);
    }

    @Getter
    @Setter
    @ToString
    public static class SAMLNameID implements Serializable {
        private static final long serialVersionUID = -7913473743778305079L;
        private String format;
        private String nameQualifier;
        private String spNameQualifier;
        private String spProviderId;
        private String value;

        public static SAMLNameID from(final NameID nameId) {
            val result = new SAMLNameID();
            result.setNameQualifier(nameId.getNameQualifier());
            result.setFormat(nameId.getFormat());
            result.setSpNameQualifier(nameId.getSPNameQualifier());
            result.setSpProviderId(nameId.getSPProvidedID());
            result.setValue(nameId.getValue());
            return result;
        }

        public static SAMLNameID from(final SAMLAttribute attribute) {
            val result = new SAMLNameID();
            result.setValue(attribute.getAttributeValues().get(0));
            result.setFormat(attribute.getNameFormat());
            result.setNameQualifier(attribute.getName());
            result.setSpNameQualifier(attribute.getFriendlyName());
            return result;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class SAMLAttribute implements Serializable {
        private static final long serialVersionUID = 2532838901563948260L;
        private String friendlyName;
        private String name;
        private String nameFormat;
        private List<String> attributeValues = new ArrayList<>();

        public static List<SAMLAttribute> from(final AttributeConverter samlAttributeConverter, final List<Attribute> samlAttributes) {

            val attributes = new ArrayList<SAMLAttribute>();

            samlAttributes.forEach(attribute -> {
                val result = samlAttributeConverter.convert(attribute);
                if (result instanceof Collection) {
                    attributes.addAll((Collection<? extends SAMLAttribute>) result);
                } else {
                    attributes.add((SAMLAttribute) result);
                }
            });

            return attributes;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class SAMLConditions implements Serializable {
        private static final long serialVersionUID = -8966585574672014553L;
        private ZonedDateTime notBefore;
        private ZonedDateTime notOnOrAfter;
    }
}
