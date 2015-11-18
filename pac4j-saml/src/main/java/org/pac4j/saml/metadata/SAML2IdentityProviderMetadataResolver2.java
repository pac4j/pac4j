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

import java.io.StringReader;
import java.util.Iterator;

import javax.annotation.Nullable;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.xml.util.XMLHelper;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.util.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;


/**
 * Alternative to {@link SAML2IdentityProviderMetadataResolver} that doesn't take a path to a metadata file but directly metadata content.
 *
 * TODO: We cannot extend {@link SAML2IdentityProviderMetadataResolver} because it needs a path in the constructor. It would be good to have
 * a common ancestor to reuse some code.
 * 
 * @author jkacer
 * @since 1.9.0
 */
public class SAML2IdentityProviderMetadataResolver2 implements SAML2MetadataResolver {

    private final String idpMetadata;
    private String idpEntityId;
    private DOMMetadataResolver idpMetadataProvider;

    
	/**
	 * Creates a new IdP metadata resolver.
	 * 
	 * @param idpMetadata
	 *            Content of the metadata.
	 * @param idpEntityId
	 *            Entity ID of the IdP.
	 */
	public SAML2IdentityProviderMetadataResolver2(final String idpMetadata, @Nullable final String idpEntityId) {
		this.idpMetadata = idpMetadata;
		this.idpEntityId = idpEntityId;
	}
	

	// Mostly copied from SAML2IdentityProviderMetadataResolver, some code removed
	@Override
	public MetadataResolver resolve() {
        try {
        	final StringReader in = new StringReader(idpMetadata);
            final Document inCommonMDDoc = Configuration.getParserPool().parse(in);
            final Element metadataRoot = inCommonMDDoc.getDocumentElement();
            idpMetadataProvider = new DOMMetadataResolver(metadataRoot);

            idpMetadataProvider.setParserPool(Configuration.getParserPool());
            idpMetadataProvider.setFailFastInitialization(true);
            idpMetadataProvider.setRequireValidMetadata(true);
            idpMetadataProvider.setId(idpMetadataProvider.getClass().getCanonicalName());
            idpMetadataProvider.initialize();


            // If no idpEntityId declared, select first EntityDescriptor entityId as our IDP entityId
            if (this.idpEntityId == null) {
                final Iterator<EntityDescriptor> it = idpMetadataProvider.iterator();

                while (it.hasNext()) {
                    final EntityDescriptor entityDescriptor = it.next();
                    if (this.idpEntityId == null) {
                        this.idpEntityId = entityDescriptor.getEntityID();
                    }
                }
            }

            if (this.idpEntityId == null) {
                throw new SAMLException("No idp entityId found");
            }

        } catch (final ComponentInitializationException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
        } catch (final XMLParserException e) {
            throw new TechnicalException("Error parsing idp Metadata", e);
        }
        return idpMetadataProvider;
	}

	
	// Copied from SAML2IdentityProviderMetadataResolver
	@Override
	public String getEntityId() {
        final XMLObject md = getEntityDescriptorElement();
        if (md instanceof EntitiesDescriptor) {
            for (final EntityDescriptor entity : ((EntitiesDescriptor) md).getEntityDescriptors()) {
                return entity.getEntityID();
            }
        } else if (md instanceof EntityDescriptor) {
            return ((EntityDescriptor) md).getEntityID();
        }
        throw new SAMLException("No idp entityId found");
	}

	
	/**
	 * Provides the path to metadata.
	 * 
	 * @return Always null.
	 * 
	 * @see org.pac4j.saml.metadata.SAML2MetadataResolver#getMetadataPath()
	 */
	@Override
	public String getMetadataPath() {
        return null; // There is no "path"
	}

	
	// Copied from SAML2IdentityProviderMetadataResolver
	@Override
	public String getMetadata() {
        return XMLHelper.nodeToString(getEntityDescriptorElement().getDOM());
	}

	
	// Copied from SAML2IdentityProviderMetadataResolver
	@Override
	public XMLObject getEntityDescriptorElement() {
        try {
            return resolve().resolveSingle(new CriteriaSet(new EntityIdCriterion(this.idpEntityId)));
        } catch (final ResolverException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
        }
	}

}
