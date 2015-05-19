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

package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2mdreqinit.RequestInitiator;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactType0004;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xml.util.XMLHelper;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SamlException;
import org.pac4j.saml.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Generates metadata object with standard values and overriden user defined values.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
@SuppressWarnings("unchecked")
public class Saml2MetadataGenerator {

    protected final static Logger logger = LoggerFactory.getLogger(Saml2MetadataGenerator.class);

    protected XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    protected MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();

    protected CredentialProvider credentialProvider;

    protected String entityId;

    protected String assertionConsumerServiceUrl;

    protected String singleLogoutServiceUrl;

    protected boolean authnRequestSigned = false;

    protected boolean wantAssertionSigned = true;

    protected int defaultACSIndex = 0;

    protected String requestInitiatorLocation = null;

    public MetadataResolver buildMetadataProvider() throws ComponentInitializationException, MarshallingException {
        final EntityDescriptor md = buildMetadata();
        final Element entityDescriptorElement = this.marshallerFactory.getMarshaller(md).marshall(md);
        final DOMMetadataResolver resolver = new DOMMetadataResolver(entityDescriptorElement);
        resolver.setRequireValidMetadata(true);
        resolver.setFailFastInitialization(true);
        resolver.setId(resolver.getClass().getCanonicalName());
        resolver.initialize();
        return resolver;
    }

    public String printMetadata() throws MarshallingException {
        final EntityDescriptor md = buildMetadata();
        final Element entityDescriptorElement = this.marshallerFactory.getMarshaller(md).marshall(md);
        return XMLHelper.nodeToString(entityDescriptorElement);
    }

    public EntityDescriptor buildMetadata() {
        final SAMLObjectBuilder<EntityDescriptor> builder = (
                SAMLObjectBuilder<EntityDescriptor>) this.builderFactory
                .getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME);

        final EntityDescriptor descriptor = builder.buildObject();
        descriptor.setEntityID(this.entityId);
        descriptor.setValidUntil(DateTime.now().plusYears(20));
        descriptor.setID(generateEntityDescriptorId());
        descriptor.setExtensions(generateMetadataExtensions());
        descriptor.getRoleDescriptors().add(buildSPSSODescriptor());

        return descriptor;

    }

    protected Extensions generateMetadataExtensions() {

        final SAMLObjectBuilder<Extensions> builderExt =
                (SAMLObjectBuilder<Extensions>) this.builderFactory
                        .getBuilder(Extensions.DEFAULT_ELEMENT_NAME);

        final Extensions extensions = builderExt.buildObject();
        extensions.getNamespaceManager().registerAttributeName(DigestMethod.TYPE_NAME);

        final SAMLObjectBuilder<DigestMethod> builder =
                (SAMLObjectBuilder<DigestMethod>) this.builderFactory
                .getBuilder(DigestMethod.DEFAULT_ELEMENT_NAME);

        DigestMethod method = builder.buildObject();
        method.setAlgorithm("http://www.w3.org/2001/04/xmlenc#sha512");
        extensions.getUnknownXMLObjects().add(method);

        method = builder.buildObject();
        method.setAlgorithm("http://www.w3.org/2001/04/xmldsig-more#sha384");
        extensions.getUnknownXMLObjects().add(method);

        method = builder.buildObject();
        method.setAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256");
        extensions.getUnknownXMLObjects().add(method);

        method = builder.buildObject();
        method.setAlgorithm("http://www.w3.org/2001/04/xmldsig-more#sha224");
        extensions.getUnknownXMLObjects().add(method);

        method = builder.buildObject();
        method.setAlgorithm("http://www.w3.org/2000/09/xmldsig#sha1");
        extensions.getUnknownXMLObjects().add(method);

        method = builder.buildObject();
        method.setAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512");
        extensions.getUnknownXMLObjects().add(method);

        method = builder.buildObject();
        method.setAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384");
        extensions.getUnknownXMLObjects().add(method);

        method = builder.buildObject();
        method.setAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        extensions.getUnknownXMLObjects().add(method);

        method = builder.buildObject();
        method.setAlgorithm("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        extensions.getUnknownXMLObjects().add(method);

        method = builder.buildObject();
        method.setAlgorithm("http://www.w3.org/2000/09/xmldsig#dsa-sha1");
        extensions.getUnknownXMLObjects().add(method);

        return extensions;
    }


    protected String generateEntityDescriptorId() {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(this.entityId.getBytes("8859_1"));
            final byte[] sourceIdDigest = messageDigest.digest();

            final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            final byte[] handle = new byte[20];
            random.nextBytes(handle);

            byte[] ENDPOINT_ID = {0, 1};
            final String id = new SAML2ArtifactType0004(ENDPOINT_ID, handle, sourceIdDigest).base64Encode();

            return id;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected SPSSODescriptor buildSPSSODescriptor() {
        final SAMLObjectBuilder<SPSSODescriptor> builder = (SAMLObjectBuilder<SPSSODescriptor>) this.builderFactory
                .getBuilder(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        final SPSSODescriptor spDescriptor = builder.buildObject();

        spDescriptor.setAuthnRequestsSigned(this.authnRequestSigned);
        spDescriptor.setWantAssertionsSigned(this.wantAssertionSigned);
        spDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
        spDescriptor.addSupportedProtocol(SAMLConstants.SAML10P_NS);
        spDescriptor.addSupportedProtocol(SAMLConstants.SAML11P_NS);

        final SAMLObjectBuilder<Extensions> builderExt =
                (SAMLObjectBuilder<Extensions>) this.builderFactory
                        .getBuilder(Extensions.DEFAULT_ELEMENT_NAME);

        final Extensions extensions = builderExt.buildObject();
        extensions.getNamespaceManager().registerAttributeName(RequestInitiator.DEFAULT_ELEMENT_NAME);

        final SAMLObjectBuilder<RequestInitiator> builderReq =
                (SAMLObjectBuilder<RequestInitiator>) this.builderFactory
                        .getBuilder(RequestInitiator.DEFAULT_ELEMENT_NAME);

        final RequestInitiator requestInitiator = builderReq.buildObject();
        requestInitiator.setLocation(this.requestInitiatorLocation);
        requestInitiator.setBinding(RequestInitiator.DEFAULT_ELEMENT_NAME.getNamespaceURI());

        extensions.getUnknownXMLObjects().add(requestInitiator);
        spDescriptor.setExtensions(extensions);

        spDescriptor.getNameIDFormats().addAll(buildNameIDFormat());

        int index = 0;
        spDescriptor.getAssertionConsumerServices().add(
                getAssertionConsumerService(SAMLConstants.SAML2_POST_BINDING_URI, index++,
                        this.defaultACSIndex == index));

        if (credentialProvider != null) {
            spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.SIGNING,
                    this.credentialProvider.getKeyInfo()));
            spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.ENCRYPTION,
                    this.credentialProvider.getKeyInfo()));
        }

        return spDescriptor;

    }

    protected Collection<NameIDFormat> buildNameIDFormat() {

        final SAMLObjectBuilder<NameIDFormat> builder = (SAMLObjectBuilder<NameIDFormat>) this.builderFactory
                .getBuilder(NameIDFormat.DEFAULT_ELEMENT_NAME);
        final Collection<NameIDFormat> formats = new LinkedList<NameIDFormat>();
        final NameIDFormat transientNameID = builder.buildObject();
        transientNameID.setFormat(NameIDType.TRANSIENT);
        formats.add(transientNameID);
        final NameIDFormat persistentNameID = builder.buildObject();
        persistentNameID.setFormat(NameIDType.PERSISTENT);
        formats.add(persistentNameID);
        final NameIDFormat emailNameID = builder.buildObject();
        emailNameID.setFormat(NameIDType.EMAIL);
        formats.add(emailNameID);
        final NameIDFormat unspecNameID = builder.buildObject();
        unspecNameID.setFormat(NameIDType.UNSPECIFIED);
        formats.add(unspecNameID);
        return formats;
    }

    protected AssertionConsumerService getAssertionConsumerService(final String binding, final int index,
            final boolean isDefault) {
        final SAMLObjectBuilder<AssertionConsumerService> builder = (SAMLObjectBuilder<AssertionConsumerService>) this.builderFactory
                .getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        final AssertionConsumerService consumer = builder.buildObject();
        consumer.setLocation(this.assertionConsumerServiceUrl);
        consumer.setBinding(binding);
        if (isDefault) {
            consumer.setIsDefault(true);
        }
        consumer.setIndex(index);
        return consumer;
    }

    protected SingleLogoutService getSingleLogoutService(final String binding) {
        final SAMLObjectBuilder<SingleLogoutService> builder = (SAMLObjectBuilder<SingleLogoutService>) this.builderFactory
                .getBuilder(SingleLogoutService.DEFAULT_ELEMENT_NAME);
        final SingleLogoutService logoutService = builder.buildObject();
        logoutService.setLocation(this.singleLogoutServiceUrl);
        logoutService.setBinding(binding);
        return logoutService;
    }

    protected KeyDescriptor getKeyDescriptor(final UsageType type, final KeyInfo key) {
        final SAMLObjectBuilder<KeyDescriptor> builder = (SAMLObjectBuilder<KeyDescriptor>)
                Configuration.getBuilderFactory()
                .getBuilder(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        final KeyDescriptor descriptor = builder.buildObject();
        descriptor.setUse(type);
        descriptor.setKeyInfo(key);
        return descriptor;
    }

    public CredentialProvider getCredentialProvider() {
        return this.credentialProvider;
    }

    public void setCredentialProvider(final CredentialProvider credentialProvider) {
        this.credentialProvider = credentialProvider;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public void setEntityId(final String entityId) {
        this.entityId = entityId;
    }

    public boolean isAuthnRequestSigned() {
        return this.authnRequestSigned;
    }

    public void setAuthnRequestSigned(final boolean authnRequestSigned) {
        this.authnRequestSigned = authnRequestSigned;
    }

    public boolean isWantAssertionSigned() {
        return this.wantAssertionSigned;
    }

    public void setWantAssertionSigned(final boolean wantAssertionSigned) {
        this.wantAssertionSigned = wantAssertionSigned;
    }

    public int getDefaultACSIndex() {
        return this.defaultACSIndex;
    }

    public void setDefaultACSIndex(final int defaultACSIndex) {
        this.defaultACSIndex = defaultACSIndex;
    }

    public void setAssertionConsumerServiceUrl(final String assertionConsumerServiceUrl) {
        this.assertionConsumerServiceUrl = assertionConsumerServiceUrl;
    }

    public void setSingleLogoutServiceUrl(final String singleLogoutServiceUrl) {
        this.singleLogoutServiceUrl = singleLogoutServiceUrl;
    }

    public void setRequestInitiatorLocation(String requestInitiatorLocation) {
        this.requestInitiatorLocation = requestInitiatorLocation;
    }
}
