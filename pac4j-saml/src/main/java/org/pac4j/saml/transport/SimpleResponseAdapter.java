/*
  Copyright 2012 -2014 pac4j organization

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

import org.pac4j.core.context.WebContext;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Empty response adapter containing a {@link ByteArrayOutputStream} in order opensaml can write
 * the saml messages. The content can be retrieved as a String from getOutgoingContent().
 * 
 * @author Michael Remond
 * @author Misagh Moayyed
 * @since 1.5.0
 *
 */
public class SimpleResponseAdapter {
    private final Pac4jServletOutputStream outputStream = new Pac4jServletOutputStream();
    private final WebContext webContext;
    private String redirectUrl;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response the response
     * @throws IllegalArgumentException if the response is null
     */
    public SimpleResponseAdapter(final WebContext response) {
        webContext = response;
    }

    public final String getOutgoingContent() {
        return outputStream.getOutgoingContent();
    }

    public final ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    public final void sendRedirect(final String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public final String getRedirectUrl() {
        return this.redirectUrl;
    }

    private static class Pac4jServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        @Override
        public final void write(final int b) throws IOException {
            outputStream.write(b);
        }

        public final String getOutgoingContent() {
            try {
                final String result = new String(this.outputStream.toByteArray(), "UTF-8");
                return result;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
