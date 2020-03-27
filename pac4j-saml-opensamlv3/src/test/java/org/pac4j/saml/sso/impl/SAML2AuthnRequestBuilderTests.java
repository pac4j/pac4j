package org.pac4j.saml.sso.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.pac4j.saml.client.AbstractSAML2ClientTests;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;

import static org.mockito.Mockito.*;

import org.opensaml.saml.saml2.core.AuthnRequest;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.store.SAMLMessageStore;
import org.pac4j.saml.store.SAMLMessageStoreFactory;


/**
 * This is {@link SAML2AuthnRequestBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class SAML2AuthnRequestBuilderTests extends AbstractSAML2ClientTests {
    
    @Test
    public void testHttpSessionStoreGetterAndSetter() {
        final SAML2Configuration configuration = getSaml2Configuration();
        configuration.setAssertionConsumerServiceIndex(1);
        
        WebContext webContext = MockWebContext.create();
        
        SAMLMessageStoreFactory messageStoreFactory = configuration.getSamlMessageStoreFactory();
        SAMLMessageStore store = messageStoreFactory.getMessageStore(webContext);
        
        final SAML2AuthnRequestBuilder builder = new SAML2AuthnRequestBuilder(configuration);

        final SAMLSelfEntityContext selfEntityContext = mock(SAMLSelfEntityContext.class);
        when(selfEntityContext.getEntityId()).thenReturn("entity-id");

        final AssertionConsumerService acs = mock(AssertionConsumerService.class);
        when(acs.getLocation()).thenReturn("https://pac4j.org");
        final SingleSignOnService ssoService = mock(SingleSignOnService.class);
        final SAML2MessageContext context = mock(SAML2MessageContext.class);
        
        when(context.getIDPSingleSignOnService(anyString())).thenReturn(ssoService);
        when(context.getSPAssertionConsumerService(anyString())).thenReturn(acs);
        when(context.getSAMLSelfEntityContext()).thenReturn(selfEntityContext);
        
        AuthnRequest authnRequest = builder.build(context);
        authnRequest.setAssertionConsumerServiceURL("https://pac4j.org");
        
        store.set(authnRequest.getID(), authnRequest);
        
        assertNotNull(store.get(authnRequest.getID()));
        assertEquals("https://pac4j.org", authnRequest.getAssertionConsumerServiceURL());
    }
    
    @Test
    public void testBuildAuthnRequestWithNoProviderAndNameIdPolicyAllowCreate() {
        final SAML2Configuration configuration = getSaml2Configuration();
        configuration.setAssertionConsumerServiceIndex(1);
        configuration.setProviderName(null);
        configuration.setUseNameQualifier(true);
        configuration.setNameIdPolicyFormat("sample-nameid-format");
        configuration.setNameIdPolicyAllowCreate(null);
        final SAML2AuthnRequestBuilder builder = new SAML2AuthnRequestBuilder(configuration);

        final SAMLSelfEntityContext selfEntityContext = mock(SAMLSelfEntityContext.class);
        when(selfEntityContext.getEntityId()).thenReturn("entity-id");

        final AssertionConsumerService acs = mock(AssertionConsumerService.class);
        when(acs.getLocation()).thenReturn("https://pac4j.org");
        final SingleSignOnService ssoService = mock(SingleSignOnService.class);
        final SAML2MessageContext context = mock(SAML2MessageContext.class);
        
        when(context.getIDPSingleSignOnService(anyString())).thenReturn(ssoService);
        when(context.getSPAssertionConsumerService(anyString())).thenReturn(acs);
        when(context.getSAMLSelfEntityContext()).thenReturn(selfEntityContext);

        assertNotNull(builder.build(context));
    }

    @Override
    protected String getCallbackUrl() {
        return "http://localhost:8080/callback?client_name=" + SAML2Client.class.getSimpleName();
    }

    @Override
    protected String getAuthnRequestBindingType() {
        return SAMLConstants.SAML2_POST_BINDING_URI;
    }
}
