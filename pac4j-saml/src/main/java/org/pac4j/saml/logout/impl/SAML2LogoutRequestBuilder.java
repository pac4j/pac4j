package org.pac4j.saml.logout.impl;

import lombok.val;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.SAML2Utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Build a SAML2 Logout Request
 *
 * @author Matthieu Taggiasco
 * @since 2.0.0
 */
public class SAML2LogoutRequestBuilder {

    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    private String bindingType;

    private boolean useNameQualifier;

    private int issueInstantSkewSeconds = 0;

    /**
     * Instantiates a new Saml 2 logout request builder.
     */
    public SAML2LogoutRequestBuilder(final SAML2Configuration cfg) {
        this.bindingType = cfg.getSpLogoutRequestBindingType();
        this.useNameQualifier = cfg.isUseNameQualifier();
    }

    public LogoutRequest build(final SAML2MessageContext context, final SAML2Profile profile) {
        val ssoService = context.getIDPSingleLogoutService(this.bindingType);
        return buildLogoutRequest(context, ssoService, profile);
    }

    @SuppressWarnings("unchecked")
    protected final LogoutRequest buildLogoutRequest(final SAML2MessageContext context,
                                                     final SingleLogoutService ssoService,
                                                     final SAML2Profile profile) {

        val builder = (SAMLObjectBuilder<LogoutRequest>) this.builderFactory
            .getBuilder(LogoutRequest.DEFAULT_ELEMENT_NAME);
        val request = builder.buildObject();

        val selfContext = context.getSAMLSelfEntityContext();

        request.setID(SAML2Utils.generateID());
        request.setIssuer(getIssuer(selfContext.getEntityId()));
        request.setIssueInstant(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(this.issueInstantSkewSeconds).toInstant());
        request.setVersion(SAMLVersion.VERSION_20);
        request.setDestination(ssoService.getLocation());

        // name id added (id of profile)
        val nameIdBuilder = (SAMLObjectBuilder<NameID>) this.builderFactory
            .getBuilder(NameID.DEFAULT_ELEMENT_NAME);
        val nameId = nameIdBuilder.buildObject();
        nameId.setValue(profile.getId());
        nameId.setFormat(profile.getSamlNameIdFormat());
        if (this.useNameQualifier) {
            nameId.setNameQualifier(profile.getSamlNameIdNameQualifier());
            nameId.setSPNameQualifier(profile.getSamlNameIdSpNameQualifier());
            nameId.setSPProvidedID(profile.getSamlNameIdSpProviderId());
        }
        request.setNameID(nameId);
        // session index added
        val sessIdx = profile.getSessionIndex();
        if (sessIdx != null) {
            val sessionIndexBuilder = (SAMLObjectBuilder<SessionIndex>) this.builderFactory
                .getBuilder(SessionIndex.DEFAULT_ELEMENT_NAME);
            val sessionIdx = sessionIndexBuilder.buildObject();
            sessionIdx.setValue(sessIdx);
            request.getSessionIndexes().add(sessionIdx);
        }
        return request;
    }

    @SuppressWarnings("unchecked")
    protected final Issuer getIssuer(final String spEntityId) {
        val issuerBuilder = (SAMLObjectBuilder<Issuer>) this.builderFactory
            .getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        val issuer = issuerBuilder.buildObject();
        issuer.setValue(spEntityId);
        return issuer;
    }

    public void setIssueInstantSkewSeconds(final int issueInstantSkewSeconds) {
        this.issueInstantSkewSeconds = issueInstantSkewSeconds;
    }
}
