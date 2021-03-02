package org.pac4j.saml.sso.impl;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.saml.client.AbstractSAML2ClientTests;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;

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

        final var messageStoreFactory = configuration.getSamlMessageStoreFactory();
        final var store = messageStoreFactory.getMessageStore(webContext, new MockSessionStore());

        final var builder = new SAML2AuthnRequestBuilder();
        final var context = buildContext();

        final var authnRequest = builder.build(context);
        authnRequest.setAssertionConsumerServiceURL("https://pac4j.org");

        store.set(authnRequest.getID(), authnRequest);

        assertNotNull(store.get(authnRequest.getID()));
        assertEquals("https://pac4j.org", authnRequest.getAssertionConsumerServiceURL());
    }

    private SAML2MessageContext buildContext() {
        final var acs = mock(AssertionConsumerService.class);
        when(acs.getLocation()).thenReturn("https://pac4j.org");
        when(acs.getIndex()).thenReturn(configuration.getAssertionConsumerServiceIndex());

        final var ssoService = mock(SingleSignOnService.class);
        when(ssoService.getBinding()).thenReturn(getSaml2Configuration().getAuthnRequestBindingType());

        final var idpDescriptor = mock(IDPSSODescriptor.class);
        when(idpDescriptor.getSingleSignOnServices()).thenReturn(Collections.singletonList(ssoService));

        final var spDescriptor = mock(SPSSODescriptor.class);
        when(spDescriptor.getAssertionConsumerServices()).thenReturn(Collections.singletonList(acs));

        final var context = new SAML2MessageContext();
        context.getSAMLPeerMetadataContext().setRoleDescriptor(idpDescriptor);
        context.getSAMLSelfMetadataContext().setRoleDescriptor(spDescriptor);
        context.getSAMLSelfEntityContext().setEntityId("entity-id");
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(configuration);
        return context;
    }

    @Test
    public void testBuildAuthnRequestWithNoProviderAndNameIdPolicyAllowCreate() {
        configuration.setProviderName(null);
        configuration.setUseNameQualifier(true);
        configuration.setNameIdPolicyFormat("sample-nameid-format");
        configuration.setNameIdPolicyAllowCreate(null);
        final var builder = new SAML2AuthnRequestBuilder();
        final var context = buildContext();
        assertNotNull(builder.build(context));
    }

    @Test
    public void testForceAuthAsRequestAttribute() {
        final var builder = new SAML2AuthnRequestBuilder();
        final var context = buildContext();
        context.getWebContext().setRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN, true);
        assertNotNull(builder.build(context));
    }

    @Test
    public void testPassiveAuthAsRequestAttribute() {
        final var builder = new SAML2AuthnRequestBuilder();
        final var context = buildContext();
        context.getWebContext().setRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE, true);
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
