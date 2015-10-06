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
package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

/**
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2ServiceProviderMetadataResolver implements SAML2MetadataResolver {
    protected final static Logger logger = LoggerFactory.getLogger(SAML2ServiceProviderMetadataResolver.class);

    private final CredentialProvider credentialProvider;
    private String spEntityId;
    private final String spMetadataPath;
    private String spMetadata;
    private final String callbackUrl;
    private final boolean forceSpMetadataGeneration;

    public SAML2ServiceProviderMetadataResolver(final CredentialProvider credentialProvider, final String spMetadataPath,
                                                final String callbackUrl) {
        this(spMetadataPath, callbackUrl, null, false, credentialProvider);
    }

    public SAML2ServiceProviderMetadataResolver(final String spMetadataPath,
                                                final String callbackUrl,
                                                @Nullable final String spEntityId,
                                                final boolean forceSpMetadataGeneration,
                                                final CredentialProvider credentialProvider) {
        this.spMetadataPath = spMetadataPath;
        this.spEntityId = spEntityId;
        this.credentialProvider = credentialProvider;
        this.callbackUrl = callbackUrl;
        this.forceSpMetadataGeneration = forceSpMetadataGeneration;

        // If the spEntityId is blank, use the callback url
        try {
            if (CommonHelper.isBlank(this.spEntityId)) {
                final URL url = new URL(callbackUrl);
                if (url.getQuery() != null) {
                    this.spEntityId = url.toString().replace("?" + url.getQuery(), "");
                } else {
                    this.spEntityId = url.toString();
                }
            }
            logger.info("Using SP entity ID {}", this.spEntityId);
        } catch (final Exception e) {
            throw new SAMLException(e);
        }
    }

    @Override
    public final MetadataResolver resolve() {
        try {
            final SAML2MetadataGenerator metadataGenerator = new SAML2MetadataGenerator();
            if (this.credentialProvider != null) {
                metadataGenerator.setCredentialProvider(this.credentialProvider);
                metadataGenerator.setAuthnRequestSigned(true);
            }

            metadataGenerator.setEntityId(this.spEntityId);
            metadataGenerator.setRequestInitiatorLocation(callbackUrl);
            // Assertion consumer service url is the callback url
            metadataGenerator.setAssertionConsumerServiceUrl(callbackUrl);
            // for now same for logout url
            metadataGenerator.setSingleLogoutServiceUrl(callbackUrl);
            final MetadataResolver spMetadataProvider = metadataGenerator.buildMetadataResolver();

            // Initialize metadata provider for our SP and get the XML as a String
            this.spMetadata = metadataGenerator.getMetadata();
            if (this.spMetadataPath != null) {

                final File file = new File(this.spMetadataPath);
                if (file.exists() && !this.forceSpMetadataGeneration) {
                    logger.info("Metadata file already exists at {}.", this.spMetadataPath);
                } else {
                    logger.info("Writing sp metadata to {}", this.spMetadataPath);

                    final Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                    final StreamResult result = new StreamResult(new StringWriter());
                    final StreamSource source = new StreamSource(new StringReader(this.spMetadata));
                    transformer.transform(source, result);
                    final FileWriter writer = new FileWriter(this.spMetadataPath);
                    writer.write(result.getWriter().toString());
                    writer.close();
                }
            }
            return spMetadataProvider;
        } catch (final ComponentInitializationException e) {
            throw new TechnicalException("Error initializing spMetadataProvider", e);
        } catch (final MarshallingException e) {
            logger.warn("Unable to marshal SP metadata", e);
        } catch (final IOException e) {
            logger.warn("Unable to print SP metadata", e);
        } catch (final Exception e) {
            logger.warn("Unable to transform metadata", e);
        }
        return null;
    }

    @Override
    public final String getEntityId() {
        return this.spEntityId;
    }

    @Override
    public String getMetadataPath() {
        return this.spMetadataPath;
    }

    @Override
    public String getMetadata() {
        return this.spMetadata;
    }

    @Override
    public XMLObject getEntityDescriptorElement() {
        try {
            return resolve().resolveSingle(new CriteriaSet(new EntityIdCriterion(getEntityId())));
        } catch (final ResolverException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
        }
    }
}
