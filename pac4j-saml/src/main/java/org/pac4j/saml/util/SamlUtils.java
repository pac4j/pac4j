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

package org.pac4j.saml.util;

import java.util.List;

import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.pac4j.saml.exceptions.SamlException;

/**
 * Utility class for SAML operations.
 * 
 * @author Michael Remond
 * @since 1.5.0
 *
 */
public class SamlUtils {

    public static SingleSignOnService getSingleSignOnService(final IDPSSODescriptor idpssoDescriptor,
            final String binding) {

        List<SingleSignOnService> services = idpssoDescriptor.getSingleSignOnServices();
        for (SingleSignOnService service : services) {
            if (service.getBinding().equals(binding)) {
                return service;
            }
        }
        throw new SamlException("Identity provider has no single sign on service available for the selected profile"
                + idpssoDescriptor);

    }

    public static AssertionConsumerService getAssertionConsumerService(final SPSSODescriptor spDescriptor,
            final Integer acsIndex) {

        List<AssertionConsumerService> services = spDescriptor.getAssertionConsumerServices();

        // Get by index
        if (acsIndex != null) {
            for (AssertionConsumerService service : services) {
                if (acsIndex.equals(service.getIndex())) {
                    return service;
                }
            }
            throw new SamlException("Assertion consumer service with index " + acsIndex
                    + " could not be found for spDescriptor " + spDescriptor);
        }

        // Get default
        if (spDescriptor.getDefaultAssertionConsumerService() != null) {
            return spDescriptor.getDefaultAssertionConsumerService();
        }

        // Get first
        if (services.size() > 0) {
            return services.iterator().next();
        }

        throw new SamlException("No assertion consumer services could be found for " + spDescriptor);

    }

}
