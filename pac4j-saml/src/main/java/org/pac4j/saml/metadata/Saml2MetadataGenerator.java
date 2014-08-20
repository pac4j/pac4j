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

import java.util.Collection;
import java.util.LinkedList;

import org.opensaml.Configuration;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml2.metadata.NameIDFormat;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.provider.AbstractMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.keyinfo.KeyInfoGenerator;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.util.XMLHelper;
import org.pac4j.saml.client.Saml2Client;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SamlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

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

    protected boolean authnRequestSigned = true;

    protected boolean wantAssertionSigned = true;

    protected int defaultACSIndex = 0;

    public AbstractMetadataProvider buildMetadataProvider() {
        final EntityDescriptor md = buildMetadata();
        return new AbstractMetadataProvider() {

            @Override
            protected XMLObject doGetMetadata() throws MetadataProviderException {
                return md;
            }
        };
    }

    public String printMetadata() throws MarshallingException {
        EntityDescriptor md = buildMetadata();
        Element entityDescriptorElement = this.marshallerFactory.getMarshaller(md).marshall(md);
        return XMLHelper.nodeToString(entityDescriptorElement);
    }

    public EntityDescriptor buildMetadata() {

        SAMLObjectBuilder<EntityDescriptor> builder = (SAMLObjectBuilder<EntityDescriptor>) this.builderFactory
                .getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        EntityDescriptor descriptor = builder.buildObject();
        descriptor.setEntityID(this.entityId);
        descriptor.getRoleDescriptors().add(buildSPSSODescriptor());

        return descriptor;

    }

    protected KeyInfo generateKeyInfoForCredential(final Credential credential) {
        try {
            KeyInfoGenerator keyInfoGenerator = SecurityHelper.getKeyInfoGenerator(credential, null,
                    Saml2Client.SAML_METADATA_KEY_INFO_GENERATOR);
            return keyInfoGenerator.generate(credential);
        } catch (org.opensaml.xml.security.SecurityException e) {
            throw new SamlException("Unable to generate keyInfo from given credential", e);
        }
    }

    protected SPSSODescriptor buildSPSSODescriptor() {

        SAMLObjectBuilder<SPSSODescriptor> builder = (SAMLObjectBuilder<SPSSODescriptor>) this.builderFactory
                .getBuilder(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        SPSSODescriptor spDescriptor = builder.buildObject();

        spDescriptor.setAuthnRequestsSigned(this.authnRequestSigned);
        spDescriptor.setWantAssertionsSigned(this.wantAssertionSigned);
        spDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);

        spDescriptor.getNameIDFormats().addAll(buildNameIDFormat());

        int index = 0;
        spDescriptor.getAssertionConsumerServices().add(
                getAssertionConsumerService(SAMLConstants.SAML2_POST_BINDING_URI, index++,
                        this.defaultACSIndex == index));

        spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.SIGNING, getKeyInfo()));
        spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.ENCRYPTION, getKeyInfo()));

        return spDescriptor;

    }

    protected Collection<NameIDFormat> buildNameIDFormat() {

        SAMLObjectBuilder<NameIDFormat> builder = (SAMLObjectBuilder<NameIDFormat>) this.builderFactory
                .getBuilder(NameIDFormat.DEFAULT_ELEMENT_NAME);
        Collection<NameIDFormat> formats = new LinkedList<NameIDFormat>();
        NameIDFormat transientNameID = builder.buildObject();
        transientNameID.setFormat(NameIDType.TRANSIENT);
        formats.add(transientNameID);
        NameIDFormat persistentNameID = builder.buildObject();
        persistentNameID.setFormat(NameIDType.PERSISTENT);
        formats.add(persistentNameID);
        NameIDFormat emailNameID = builder.buildObject();
        emailNameID.setFormat(NameIDType.EMAIL);
        formats.add(emailNameID);
        NameIDFormat unspecNameID = builder.buildObject();
        unspecNameID.setFormat(NameIDType.UNSPECIFIED);
        formats.add(unspecNameID);
        return formats;
    }

    protected AssertionConsumerService getAssertionConsumerService(final String binding, final int index,
            final boolean isDefault) {
        SAMLObjectBuilder<AssertionConsumerService> builder = (SAMLObjectBuilder<AssertionConsumerService>) this.builderFactory
                .getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        AssertionConsumerService consumer = builder.buildObject();
        consumer.setLocation(this.assertionConsumerServiceUrl);
        consumer.setBinding(binding);
        if (isDefault) {
            consumer.setIsDefault(true);
        }
        consumer.setIndex(index);
        return consumer;
    }

    protected SingleLogoutService getSingleLogoutService(final String binding) {
        SAMLObjectBuilder<SingleLogoutService> builder = (SAMLObjectBuilder<SingleLogoutService>) this.builderFactory
                .getBuilder(SingleLogoutService.DEFAULT_ELEMENT_NAME);
        SingleLogoutService logoutService = builder.buildObject();
        logoutService.setLocation(this.singleLogoutServiceUrl);
        logoutService.setBinding(binding);
        return logoutService;
    }

    protected KeyDescriptor getKeyDescriptor(final UsageType type, final KeyInfo key) {
        SAMLObjectBuilder<KeyDescriptor> builder = (SAMLObjectBuilder<KeyDescriptor>) Configuration.getBuilderFactory()
                .getBuilder(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        KeyDescriptor descriptor = builder.buildObject();
        descriptor.setUse(type);
        descriptor.setKeyInfo(key);
        return descriptor;
    }

    protected KeyInfo getKeyInfo() {
        Credential serverCredential = this.credentialProvider.getCredential();
        return generateKeyInfoForCredential(serverCredential);
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
}
