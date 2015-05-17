/*
  Copyright 2012 -2014 Michael Remond

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package org.pac4j.saml.sso;

import java.util.Random;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.SamlUtils;

/**
 * Build a SAML2 Authn Request from the given {@link MessageContext}.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
@SuppressWarnings("rawtypes")
public class Saml2AuthnRequestBuilder {

    private boolean forceAuth;

    private AuthnContextComparisonTypeEnumeration comparisonType;

    private String bindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String authnContextClassRef = null;

    private String nameIdPolicyFormat = null;

    /**
     * Default constructor
     */
    public Saml2AuthnRequestBuilder() {
    }

    /**
     * @param forceAuth
     * @param comparisonType
     * @param bindingType
     * @param authnContextClassRef
     * @param nameIdPolicyFormat
     */
    public Saml2AuthnRequestBuilder(boolean forceAuth, String comparisonType, String bindingType, 
            String authnContextClassRef, String nameIdPolicyFormat) {
        this.forceAuth = forceAuth;
        this.comparisonType = getComparisonTypeEnumFromString(comparisonType);
        this.bindingType = bindingType;
        this.authnContextClassRef = authnContextClassRef;
        this.nameIdPolicyFormat = nameIdPolicyFormat;
    }

    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    public AuthnRequest build(final MessageContext<SAMLObject> context) {

        SAMLSelfEntityContext selfContext = context.getSubcontext(SAMLSelfEntityContext.class);
        SAMLPeerEntityContext peerContext = context.getSubcontext(SAMLPeerEntityContext.class);

        SPSSODescriptor spDescriptor = (SPSSODescriptor) selfContext.getRole();
        IDPSSODescriptor idpssoDescriptor = (IDPSSODescriptor) peerContext.getRole();

        SingleSignOnService ssoService = SamlUtils.getSingleSignOnService(idpssoDescriptor, bindingType);
        AssertionConsumerService assertionConsumerService = SamlUtils.getAssertionConsumerService(spDescriptor, null);

        return buildAuthnRequest(context, assertionConsumerService, ssoService);
    }

    @SuppressWarnings("unchecked")
    protected AuthnRequest buildAuthnRequest(final MessageContext<SAMLObject> context,
            final AssertionConsumerService assertionConsumerService, final SingleSignOnService ssoService) {

        SAMLObjectBuilder<AuthnRequest> builder = (SAMLObjectBuilder<AuthnRequest>) this.builderFactory
                .getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
        AuthnRequest request = builder.buildObject();
        if (comparisonType != null) {
            RequestedAuthnContext authnContext = new RequestedAuthnContextBuilder().buildObject();
            authnContext.setComparison(comparisonType);

            if (authnContextClassRef != null) {
                AuthnContextClassRef classRef = new AuthnContextClassRefBuilder().buildObject();
                classRef.setAuthnContextClassRef(authnContextClassRef);
                authnContext.getAuthnContextClassRefs().add(classRef);
            }
            request.setRequestedAuthnContext(authnContext);
        }

        SAMLSelfEntityContext selfContext = context.getSubcontext(SAMLSelfEntityContext.class);

        request.setID(generateID());
        request.setIssuer(getIssuer(selfContext.getEntityId()));
        request.setIssueInstant(new DateTime());
        request.setVersion(SAMLVersion.VERSION_20);
        request.setIsPassive(false);
        request.setForceAuthn(this.forceAuth);
        request.setProviderName("pac4j-saml");

        if (nameIdPolicyFormat != null) {
            NameIDPolicy nameIdPolicy = new NameIDPolicyBuilder().buildObject();
            nameIdPolicy.setAllowCreate(true);
            nameIdPolicy.setFormat(nameIdPolicyFormat);
            request.setNameIDPolicy(nameIdPolicy);
        }

        request.setDestination(ssoService.getLocation());
        request.setAssertionConsumerServiceURL(assertionConsumerService.getLocation());
        request.setProtocolBinding(assertionConsumerService.getBinding());

        return request;

    }

    @SuppressWarnings("unchecked")
    protected Issuer getIssuer(final String spEntityId) {
        SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>) this.builderFactory
                .getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(spEntityId);
        return issuer;
    }

    protected String generateID() {
        Random r = new Random();
        return '_' + Long.toString(Math.abs(r.nextLong()), 16) + Long.toString(Math.abs(r.nextLong()), 16);
    }

    protected AuthnContextComparisonTypeEnumeration getComparisonTypeEnumFromString(String comparisonType) {
        if ("exact".equals(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.EXACT;
        } else if ("minimum".equals(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.MINIMUM;
        } else if ("maximum".equals(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.MAXIMUM;
        } else if ("better".equals(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.BETTER;
        } else {
            return null;
        }
    }
}
