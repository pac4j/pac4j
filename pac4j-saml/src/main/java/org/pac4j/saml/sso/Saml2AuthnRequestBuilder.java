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
import org.opensaml.Configuration;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.pac4j.saml.util.SamlUtils;

/**
 * Build a SAML2 Authn Request from the given {@link SAMLMessageContext}.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
@SuppressWarnings("rawtypes")
public class Saml2AuthnRequestBuilder {

    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    public AuthnRequest build(final SAMLMessageContext context) {

        SPSSODescriptor spDescriptor = (SPSSODescriptor) context.getLocalEntityRoleMetadata();
        IDPSSODescriptor idpssoDescriptor = (IDPSSODescriptor) context.getPeerEntityRoleMetadata();

        SingleSignOnService ssoService = SamlUtils.getSingleSignOnService(idpssoDescriptor,
                SAMLConstants.SAML2_POST_BINDING_URI);
        AssertionConsumerService assertionConsumerService = SamlUtils.getAssertionConsumerService(spDescriptor, null);

        return buildAuthnRequest(context, assertionConsumerService, ssoService);
    }

    @SuppressWarnings("unchecked")
    protected AuthnRequest buildAuthnRequest(final SAMLMessageContext context,
            final AssertionConsumerService assertionConsumerService, final SingleSignOnService ssoService) {

        SAMLObjectBuilder<AuthnRequest> builder = (SAMLObjectBuilder<AuthnRequest>) this.builderFactory
                .getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
        AuthnRequest request = builder.buildObject();

        request.setID(generateID());
        request.setIssuer(getIssuer(context.getLocalEntityId()));
        request.setIssueInstant(new DateTime());
        request.setVersion(SAMLVersion.VERSION_20);
        request.setIsPassive(false);
        request.setForceAuthn(false);
        request.setProviderName("pac4j-saml");

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
}
