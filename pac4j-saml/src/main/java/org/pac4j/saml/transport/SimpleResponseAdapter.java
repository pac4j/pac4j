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

import org.pac4j.core.context.J2EContext;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Empty response adapter containing a {@link ByteArrayOutputStream} in order opensaml can write
 * the saml messages. The content can be retrieved as a String from getOutgoingContent().
 * 
 * @author Michael Remond
 * @since 1.5.0
 *
 */
public class SimpleResponseAdapter extends HttpServletResponseWrapper {
    private Pac4jServletOutputStream outputStream = new Pac4jServletOutputStream();
    private String redirectUrl;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response the response
     * @throws IllegalArgumentException if the response is null
     */
    public SimpleResponseAdapter(final J2EContext response) {
        super(response.getResponse());
    }

    public String getOutgoingContent() {
        return outputStream.getOutgoingContent();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    @Override
    public void sendRedirect(final String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }


    private static class Pac4jServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
        }

        public String getOutgoingContent() {
            try {
                String result = new String(this.outputStream.toByteArray(), "UTF-8");
                return result;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
