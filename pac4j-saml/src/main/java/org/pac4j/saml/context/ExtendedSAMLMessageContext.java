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

package org.pac4j.saml.context;

import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml2.core.Assertion;

/**
 * Allow to store additional information for SAML processing.
 * 
 * @author Michael Remond
 * @version 1.5.0
 */
@SuppressWarnings("rawtypes")
public class ExtendedSAMLMessageContext extends BasicSAMLMessageContext {

    /* valid subject assertion */
    private Assertion subjectAssertion;

    /* id of the authn request */
    private String requestId;

    /* endpoint location */
    private String assertionConsumerUrl;

    public Assertion getSubjectAssertion() {
        return this.subjectAssertion;
    }

    public void setSubjectAssertion(final Assertion subjectAssertion) {
        this.subjectAssertion = subjectAssertion;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public String getAssertionConsumerUrl() {
        return this.assertionConsumerUrl;
    }

    public void setAssertionConsumerUrl(final String assertionConsumerUrl) {
        this.assertionConsumerUrl = assertionConsumerUrl;
    }
}
