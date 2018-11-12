package org.pac4j.saml.logout.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.SAML2Utils;

/**
 * Build a SAML2 Logout Request
 * 
 * @author Matthieu Taggiasco
 * @since 2.0.0
 */
public class SAML2LogoutRequestBuilder {

    private String bindingType;

    private int issueInstantSkewSeconds = 0;

    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    /**
     * Instantiates a new Saml 2 logout request builder.
     *
     * @param bindingType the binding type
     */
    public SAML2LogoutRequestBuilder(final String bindingType) {
        this.bindingType = bindingType;
    }

    public LogoutRequest build(final SAML2MessageContext context, final SAML2Profile profile) {
        final SingleLogoutService ssoService = context.getIDPSingleLogoutService(this.bindingType);
        return buildLogoutRequest(context, ssoService, profile);
    }

    @SuppressWarnings("unchecked")
    protected final LogoutRequest buildLogoutRequest(final SAML2MessageContext context,
                                                     final SingleLogoutService ssoService,
                                                     final SAML2Profile profile) {

        final SAMLObjectBuilder<LogoutRequest> builder = (SAMLObjectBuilder<LogoutRequest>) this.builderFactory
            .getBuilder(LogoutRequest.DEFAULT_ELEMENT_NAME);
        final LogoutRequest request = builder.buildObject();

        final SAMLSelfEntityContext selfContext = context.getSAMLSelfEntityContext();

        request.setID(SAML2Utils.generateID());
        request.setIssuer(getIssuer(selfContext.getEntityId()));
        request.setIssueInstant(DateTime.now(DateTimeZone.UTC).plusSeconds(this.issueInstantSkewSeconds));
        request.setVersion(SAMLVersion.VERSION_20);
        request.setDestination(ssoService.getLocation());

        // name id added (id of profile)
        final SAMLObjectBuilder<NameID> nameIdBuilder = (SAMLObjectBuilder<NameID>) this.builderFactory
            .getBuilder(NameID.DEFAULT_ELEMENT_NAME);
        final NameID nameId = nameIdBuilder.buildObject();
        nameId.setValue(profile.getId());
        nameId.setFormat(profile.getSamlNameIdFormat());
        nameId.setNameQualifier(profile.getSamlNameIdNameQualifier());
        nameId.setSPNameQualifier(profile.getSamlNameIdSpNameQualifier());
        nameId.setSPProvidedID(profile.getSamlNameIdSpProviderId());
        request.setNameID(nameId);
        // session index added
        final String sessIdx = profile.getSessionIndex();
        final SAMLObjectBuilder<SessionIndex> sessionIndexBuilder = (SAMLObjectBuilder<SessionIndex>) this.builderFactory
            .getBuilder(SessionIndex.DEFAULT_ELEMENT_NAME);
        final SessionIndex sessionIdx = sessionIndexBuilder.buildObject();
        sessionIdx.setSessionIndex(sessIdx);
        request.getSessionIndexes().add(sessionIdx);

        return request;
    }

    @SuppressWarnings("unchecked")
    protected final Issuer getIssuer(final String spEntityId) {
        final SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>) this.builderFactory
                .getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        final Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(spEntityId);
        return issuer;
    }

    public void setIssueInstantSkewSeconds(final int issueInstantSkewSeconds) {
        this.issueInstantSkewSeconds = issueInstantSkewSeconds;
    }
}
