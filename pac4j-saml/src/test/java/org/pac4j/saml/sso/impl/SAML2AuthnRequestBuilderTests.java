package org.pac4j.saml.sso.impl;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.saml.client.AbstractSAML2ClientTests;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.profile.api.SAML2ObjectBuilder;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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

        var scopedIdP = new SAML2ScopingIdentityProvider("idp-entity-id", "My Identity Provider");
        configuration.getScopingIdentityProviders().add(scopedIdP);
    }

    @Test
    public void testHttpSessionStoreGetterAndSetter() {
        final WebContext webContext = MockWebContext.create();

        val messageStoreFactory = configuration.getSamlMessageStoreFactory();
        val store = messageStoreFactory.getMessageStore(webContext, new MockSessionStore());

        SAML2ObjectBuilder<AuthnRequest> builder = new SAML2AuthnRequestBuilder();
        val context = buildContext();

        val authnRequest = builder.build(context);
        authnRequest.setAssertionConsumerServiceURL("https://pac4j.org");

        store.set(authnRequest.getID(), authnRequest);

        assertNotNull(store.get(authnRequest.getID()));
        assertEquals("https://pac4j.org", authnRequest.getAssertionConsumerServiceURL());
    }

    private SAML2MessageContext buildContext() {
        val acs = mock(AssertionConsumerService.class);
        when(acs.getLocation()).thenReturn("https://pac4j.org");
        when(acs.getIndex()).thenReturn(configuration.getAssertionConsumerServiceIndex());

        val ssoService = mock(SingleSignOnService.class);
        when(ssoService.getBinding()).thenReturn(getSaml2Configuration().getAuthnRequestBindingType());

        val idpDescriptor = mock(IDPSSODescriptor.class);
        when(idpDescriptor.getSingleSignOnServices()).thenReturn(Collections.singletonList(ssoService));

        val spDescriptor = mock(SPSSODescriptor.class);
        when(spDescriptor.getAssertionConsumerServices()).thenReturn(Collections.singletonList(acs));

        val context = new SAML2MessageContext(new CallContext(MockWebContext.create(), new MockSessionStore()));
        context.getSAMLPeerMetadataContext().setRoleDescriptor(idpDescriptor);
        context.getSAMLSelfMetadataContext().setRoleDescriptor(spDescriptor);
        context.getSAMLSelfEntityContext().setEntityId("entity-id");
        context.setSaml2Configuration(configuration);
        return context;
    }

    @Test
    public void testBuildAuthnRequestWithNoProviderAndNameIdPolicyAllowCreate() {
        configuration.setProviderName(null);
        configuration.setUseNameQualifier(true);
        configuration.setNameIdPolicyFormat("sample-nameid-format");
        configuration.setNameIdPolicyAllowCreate(null);
        SAML2ObjectBuilder<AuthnRequest> builder = new SAML2AuthnRequestBuilder();
        val context = buildContext();
        assertNotNull(builder.build(context));
    }

    @Test
    public void testForceAuthAsRequestAttribute() {
        SAML2ObjectBuilder<AuthnRequest> builder = new SAML2AuthnRequestBuilder();
        val context = buildContext();
        context.getCallContext().webContext().setRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN, true);
        assertNotNull(builder.build(context));
    }

    @Test
    public void testPassiveAuthAsRequestAttribute() {
        SAML2ObjectBuilder<AuthnRequest> builder = new SAML2AuthnRequestBuilder();
        val context = buildContext();
        context.getCallContext().webContext().setRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE, true);
        assertNotNull(builder.build(context));
    }

    @Test
    public void testScopingIdentityProviders() {
        SAML2ObjectBuilder<AuthnRequest> builder = new SAML2AuthnRequestBuilder();
        val context = buildContext();
        var authnRequest = builder.build(context);
        assertNotNull(authnRequest);
        assertNotNull(authnRequest.getScoping());
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
