/*
  Copyright 2012 - 2015 pac4j organization

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
package org.pac4j.saml.client;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.storage.EmptyStorageFactory;
import org.pac4j.saml.storage.SAMLMessageStorageFactory;


/**
 * The {@link SAML2ClientConfiguration} is responsible for...
 * capturing client settings and passing them around.
 * @author Misagh Moayyed
 * @since 1.7
 */
public final class SAML2ClientConfiguration implements Cloneable {

    private String keystorePath;

    private String keystorePassword;

    private String privateKeyPassword;

    private String identityProviderMetadataPath;

    private String identityProviderEntityId;

    private String serviceProviderEntityId;

    private int maximumAuthenticationLifetime;

    private boolean forceAuth = false;

    private String comparisonType = null;

    private String destinationBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String authnContextClassRef = null;

    private String nameIdPolicyFormat = null;

    private String serviceProviderMetadataPath;

    private boolean forceServiceProviderMetadataGeneration;

    private SAMLMessageStorageFactory samlMessageStorageFactory = new EmptyStorageFactory();

    public SAML2ClientConfiguration(final String keystorePath, final String keystorePassword,
                                    final String privateKeyPassword, final String identityProviderMetadataPath) {
        this(keystorePath, keystorePassword, privateKeyPassword, identityProviderMetadataPath, null, null);
    }

    public SAML2ClientConfiguration(final String keystorePath, final String keystorePassword,
                                    final String privateKeyPassword, final String identityProviderMetadataPath,
                                    final String identityProviderEntityId, final String serviceProviderEntityId) {
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
        this.privateKeyPassword = privateKeyPassword;
        this.identityProviderMetadataPath = identityProviderMetadataPath;
        this.identityProviderEntityId = identityProviderEntityId;
        this.serviceProviderEntityId = serviceProviderEntityId;

        CommonHelper.assertNotBlank("keystorePath", this.keystorePath);
        CommonHelper.assertNotBlank("keystorePassword", this.keystorePassword);
        CommonHelper.assertNotBlank("privateKeyPassword", this.privateKeyPassword);
        CommonHelper.assertNotBlank("identityProviderMetadataPath", this.identityProviderMetadataPath);
    }

    public void setIdentityProviderMetadataPath(final String identityProviderMetadataPath) {
        this.identityProviderMetadataPath = identityProviderMetadataPath;
    }

    public void setIdentityProviderEntityId(final String identityProviderEntityId) {
        this.identityProviderEntityId = identityProviderEntityId;
    }

    public void setServiceProviderEntityId(final String serviceProviderEntityId) {
        this.serviceProviderEntityId = serviceProviderEntityId;
    }

    public void setKeystorePath(final String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public void setKeystorePassword(final String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public void setPrivateKeyPassword(final String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    public void setMaximumAuthenticationLifetime(final int maximumAuthenticationLifetime) {
        this.maximumAuthenticationLifetime = maximumAuthenticationLifetime;
    }

    /**
     * @return the forceAuth
     */
    public boolean isForceAuth() {
        return forceAuth;
    }

    /**
     * @param forceAuth the forceAuth to set
     */
    public void setForceAuth(final boolean forceAuth) {
        this.forceAuth = forceAuth;
    }

    /**
     * @return the comparisonType
     */
    public String getComparisonType() {
        return comparisonType;
    }

    /**
     * @param comparisonType the comparisonType to set
     */
    public void setComparisonType(final String comparisonType) {
        this.comparisonType = comparisonType;
    }

    /**
     * @return the destinationBindingType
     */
    public String getDestinationBindingType() {
        return destinationBindingType;
    }

    /**
     * @param destinationBindingType the destinationBindingType to set
     */
    public void setDestinationBindingType(final String destinationBindingType) {
        this.destinationBindingType = destinationBindingType;
    }

    /**
     * @return the authnContextClassRef
     */
    public String getAuthnContextClassRef() {
        return authnContextClassRef;
    }

    /**
     * @param authnContextClassRef the authnContextClassRef to set
     */
    public void setAuthnContextClassRef(final String authnContextClassRef) {
        this.authnContextClassRef = authnContextClassRef;
    }

    /**
     * @return the nameIdPolicyFormat
     */
    public String getNameIdPolicyFormat() {
        return nameIdPolicyFormat;
    }

    /**
     * @param nameIdPolicyFormat the nameIdPolicyFormat to set
     */
    public void setNameIdPolicyFormat(final String nameIdPolicyFormat) {
        this.nameIdPolicyFormat = nameIdPolicyFormat;
    }

    public void setServiceProviderMetadataPath(final String serviceProviderMetadataPath) {
        this.serviceProviderMetadataPath = serviceProviderMetadataPath;
    }

    public void setForceServiceProviderMetadataGeneration(final boolean forceServiceProviderMetadataGeneration) {
        this.forceServiceProviderMetadataGeneration = forceServiceProviderMetadataGeneration;
    }

    public String getIdentityProviderMetadataPath() {
        return identityProviderMetadataPath;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public String getIdentityProviderEntityId() {
        return identityProviderEntityId;
    }

    public String getServiceProviderEntityId() {
        return serviceProviderEntityId;
    }

    public int getMaximumAuthenticationLifetime() {
        return maximumAuthenticationLifetime;
    }

    public String getServiceProviderMetadataPath() {
        return serviceProviderMetadataPath;
    }

    public boolean isForceServiceProviderMetadataGeneration() {
        return forceServiceProviderMetadataGeneration;
    }

    public SAMLMessageStorageFactory getSamlMessageStorageFactory() {
        return samlMessageStorageFactory;
    }

    public void setSamlMessageStorageFactory(final SAMLMessageStorageFactory samlMessageStorageFactory) {
        this.samlMessageStorageFactory = samlMessageStorageFactory;
    }

    @Override
    public SAML2ClientConfiguration clone() {
        try {
            return (SAML2ClientConfiguration) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
