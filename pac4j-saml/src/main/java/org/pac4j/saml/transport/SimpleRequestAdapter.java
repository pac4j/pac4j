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

package org.pac4j.saml.transport;

import java.io.InputStream;
import java.util.List;

import org.opensaml.ws.transport.http.HTTPInTransport;
import org.opensaml.xml.security.credential.Credential;
import org.pac4j.core.context.WebContext;

/**
 * Basic RequestAdapter returning an inputStream from the input content of
 * the {@link WebContext}.
 * 
 * @author Michael Remond
 * @since 1.5.0
 *
 */
public class SimpleRequestAdapter implements HTTPInTransport {

    private final WebContext wc;

    public WebContext getWebContext() {
        return wc;
    }

    public SimpleRequestAdapter(final WebContext wc) {
        this.wc = wc;
    }

    public InputStream getIncomingStream() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Object getAttribute(final String arg0) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Credential getLocalCredential() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Credential getPeerCredential() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isAuthenticated() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isConfidential() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isIntegrityProtected() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setAuthenticated(final boolean arg0) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setConfidential(final boolean arg0) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setIntegrityProtected(final boolean arg0) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getHTTPMethod() {
        return wc.getRequestMethod();
    }

    public String getHeaderValue(final String arg0) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getParameterValue(final String arg0) {
        return wc.getRequestParameter(arg0);
    }

    public List<String> getParameterValues(final String arg0) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public int getStatusCode() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public HTTP_VERSION getVersion() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getPeerAddress() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getPeerDomainName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
