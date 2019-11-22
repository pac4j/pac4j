/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pac4j.saml.metadata;

import java.io.IOException;
import java.util.Timer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.saml.metadata.resolver.impl.AbstractReloadingMetadataResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

/**
 * A metadata provider that pulls metadata from a file on the local filesystem.
 * 
 * This metadata provider periodically checks to see if the read metadata file has changed. The delay between each
 * refresh interval is calculated as follows. If no validUntil or cacheDuration is present then the
 * {@link #getMaxRefreshDelay()} value is used. Otherwise, the earliest refresh interval of the metadata file is checked
 * by looking for the earliest of all the validUntil attributes and cacheDuration attributes. If that refresh interval
 * is larger than the max refresh delay then {@link #getMaxRefreshDelay()} is used. If that number is smaller than the
 * min refresh delay then {@link #getMinRefreshDelay()} is used. Otherwise the calculated refresh delay multiplied by
 * {@link #getRefreshDelayFactor()} is used. By using this factor, the provider will attempt to be refresh before the
 * cache actually expires, allowing a some room for error and recovery. Assuming the factor is not exceedingly close to
 * 1.0 and a min refresh delay that is not overly large, this refresh will likely occur a few times before the cache
 * expires.
 * 
 */
public class SpringResourceMetadataResolver extends AbstractReloadingMetadataResolver {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(SpringResourceMetadataResolver.class);

    /** Resource from which metadata is read. */
    private Resource metadataResource;

    /**
     * Constructor.
     * 
     * @param resource resource from which to read the metadata file.
     * 
     * @throws ResolverException thrown if there is a problem retrieving information about the resource
     */
    public SpringResourceMetadataResolver(Resource metadataResource) throws ResolverException {
        super();
        setMetadataResource(metadataResource);
    }
    
    /**
     * Constructor.
     * 
     * @param resource resource from which to read the metadata file.
     * @param timer task timer used to schedule metadata refresh tasks
     * 
     * @throws IOException thrown if there is a problem retrieving information about the resource
     */
    public SpringResourceMetadataResolver(@Nullable Timer backgroundTaskTimer, @Nonnull Resource metadataResource) 
        throws ResolverException {
        super(backgroundTaskTimer);
        setMetadataResource(metadataResource);
    }

    /**
     * Sets the file from which metadata is read.
     * 
     * @param file path to the metadata file
     * 
     * @throws ResolverException this exception is no longer thrown
     */
    protected void setMetadataResource(@Nonnull final Resource metadataResource) throws ResolverException {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);

        this.metadataResource = Constraint.isNotNull(metadataResource, "Metadata file cannot be null");
    }


    /** {@inheritDoc} */
    protected void doDestroy() {
        metadataResource = null;
        
        super.doDestroy();
    }
    
    /** {@inheritDoc} */
    protected String getMetadataIdentifier() {
        return metadataResource.getDescription();
    }

    /** {@inheritDoc} */
    protected byte[] fetchMetadata() throws ResolverException {
        try {
            final DateTime metadataUpdateTime =
                    new DateTime(metadataResource.lastModified(), ISOChronology.getInstanceUTC());
            log.debug("resource {} was last modified {}", metadataResource.getDescription(), metadataUpdateTime);
            if (getLastRefresh() == null || metadataUpdateTime.isAfter(getLastRefresh())) {
                return inputstreamToByteArray(metadataResource.getInputStream());
            }

            return null;
        } catch (IOException e) {
            String errorMsg = "Unable to read metadata file";
            log.error(errorMsg, e);
            throw new ResolverException(errorMsg, e);
        }
    }
    
    /**
     * Validate the basic properties of the specified metadata file, for example that it exists; 
     * that it is a file; and that it is readable.
     *
     * @param file the file to evaluate
     * @throws ResolverException if file does not pass basic properties required of a metadata file
     */
    protected void validateMetadataResource(@Nonnull final Resource metadataResource) throws ResolverException {
        if (!metadataResource.exists()) {
            throw new ResolverException("Metadata file '" + metadataResource.getDescription() + "' does not exist");
        }

        if (!metadataResource.isReadable()) {
            throw new ResolverException("Metadata file '" + metadataResource.getDescription() + "' is not readable");
        }
    }

}
