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

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.security.credential.Credential;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;


/**
 * Empty response adapter containing a {@link ByteArrayOutputStream} in order opensaml can write
 * the saml messages. The content can be retrieved as a String from getOutgoingContent().
 * 
 * @author Michael Remond
 * @since 1.5.0
 *
 */
public class SimpleResponseAdapter extends MessageContext<SAMLObject> {

    private final OutputStream outputStream = new ByteArrayOutputStream();

    private String redirectUrl;

    public String getOutgoingContent() {
        return outputStream.toString();
    }

    public OutputStream getOutgoingStream() {
        return outputStream;
    }

    public void sendRedirect(final String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    /**
     * @return the redirectUrl
     */
    public String getRedirectUrl() {
        return this.redirectUrl;
    }
}
