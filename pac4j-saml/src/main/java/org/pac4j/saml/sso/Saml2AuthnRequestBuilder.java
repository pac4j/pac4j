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

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
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
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;
import org.pac4j.saml.util.Configuration;

import java.util.Random;

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

    public AuthnRequest build(final ExtendedSAMLMessageContext context) {
        final SingleSignOnService ssoService = context.getIDPSingleSignOnService(this.bindingType);
        final AssertionConsumerService assertionConsumerService = context.getSPAssertionConsumerService();

        return buildAuthnRequest(context, assertionConsumerService, ssoService);
    }

    @SuppressWarnings("unchecked")
    protected AuthnRequest buildAuthnRequest(final ExtendedSAMLMessageContext context,
            final AssertionConsumerService assertionConsumerService, final SingleSignOnService ssoService) {

        final SAMLObjectBuilder<AuthnRequest> builder = (SAMLObjectBuilder<AuthnRequest>) this.builderFactory
                .getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
        final AuthnRequest request = builder.buildObject();
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

        SAMLSelfEntityContext selfContext = context.getSAMLSelfEntityContext();

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
        if ("exact".equalsIgnoreCase(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.EXACT;
        }
        if ("minimum".equalsIgnoreCase(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.MINIMUM;
        }
        if ("maximum".equalsIgnoreCase(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.MAXIMUM;
        }
        if ("better".equalsIgnoreCase(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.BETTER;
        }
        return null;
    }
}
