package org.pac4j.saml.sso.impl;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.saml.client.AbstractSAML2ClientTests;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.store.SAMLMessageStore;
import org.pac4j.saml.store.SAMLMessageStoreFactory;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;


/**
 * This is {@link SAML2AuthnRequestBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class SAML2AuthnRequestBuilderTests extends AbstractSAML2ClientTests {
    private SAML2Configuration configuration;

    @Before
    public void setup() {
        configuration = getSaml2Configuration();
        configuration.setAssertionConsumerServiceIndex(1);
    }

    @Test
    public void testHttpSessionStoreGetterAndSetter() {
        final WebContext webContext = MockWebContext.create();

        final SAMLMessageStoreFactory messageStoreFactory = configuration.getSamlMessageStoreFactory();
        final SAMLMessageStore store = messageStoreFactory.getMessageStore(webContext, new MockSessionStore());

        final SAML2AuthnRequestBuilder builder = new SAML2AuthnRequestBuilder(configuration);

        final SAML2MessageContext context = buildContext();

        final AuthnRequest authnRequest = builder.build(context);
        authnRequest.setAssertionConsumerServiceURL("https://pac4j.org");

        store.set(authnRequest.getID(), authnRequest);

        assertNotNull(store.get(authnRequest.getID()));
        assertEquals("https://pac4j.org", authnRequest.getAssertionConsumerServiceURL());
    }

    private SAML2MessageContext buildContext() {
        final AssertionConsumerService acs = mock(AssertionConsumerService.class);
        when(acs.getLocation()).thenReturn("https://pac4j.org");
        when(acs.getIndex()).thenReturn(configuration.getAssertionConsumerServiceIndex());

        final SingleSignOnService ssoService = mock(SingleSignOnService.class);
        when(ssoService.getBinding()).thenReturn(getSaml2Configuration().getAuthnRequestBindingType());

        final IDPSSODescriptor idpDescriptor = mock(IDPSSODescriptor.class);
        when(idpDescriptor.getSingleSignOnServices()).thenReturn(Collections.singletonList(ssoService));

        final SPSSODescriptor spDescriptor = mock(SPSSODescriptor.class);
        when(spDescriptor.getAssertionConsumerServices()).thenReturn(Collections.singletonList(acs));

        final SAML2MessageContext context = new SAML2MessageContext();
        context.getSAMLPeerMetadataContext().setRoleDescriptor(idpDescriptor);
        context.getSAMLSelfMetadataContext().setRoleDescriptor(spDescriptor);
        context.getSAMLSelfEntityContext().setEntityId("entity-id");
        return context;
    }

    @Test
    public void testBuildAuthnRequestWithNoProviderAndNameIdPolicyAllowCreate() {
        configuration.setProviderName(null);
        configuration.setUseNameQualifier(true);
        configuration.setNameIdPolicyFormat("sample-nameid-format");
        configuration.setNameIdPolicyAllowCreate(null);
        final SAML2AuthnRequestBuilder builder = new SAML2AuthnRequestBuilder(configuration);
        final SAML2MessageContext context = buildContext();
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
