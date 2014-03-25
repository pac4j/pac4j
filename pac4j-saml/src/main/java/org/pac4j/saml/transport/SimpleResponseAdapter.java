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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.opensaml.ws.transport.http.HTTPOutTransport;
import org.opensaml.xml.security.credential.Credential;

/**
 * Empty response adapter containing a {@link ByteArrayOutputStream} in order opensaml can write
 * the saml messages. The content can be retrieved as a String from getOutgoingContent().
 * 
 * @author Michael Remond
 * @since 1.5.0
 *
 */
public class SimpleResponseAdapter implements HTTPOutTransport {

    private final OutputStream outputStream = new ByteArrayOutputStream();

    public String getOutgoingContent() {
        return outputStream.toString();
    }

    public OutputStream getOutgoingStream() {
        return outputStream;
    }

    public void setAttribute(final String arg0, final Object arg1) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setCharacterEncoding(final String arg0) {
        // TODO implement
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
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getHeaderValue(final String arg0) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getParameterValue(final String arg0) {
        throw new UnsupportedOperationException("Not implemented yet");
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

    public void addParameter(final String arg0, final String arg1) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void sendRedirect(final String arg0) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setHeader(final String arg0, final String arg1) {
        // TODO implement
    }

    public void setStatusCode(final int arg0) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setVersion(final HTTP_VERSION arg0) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
