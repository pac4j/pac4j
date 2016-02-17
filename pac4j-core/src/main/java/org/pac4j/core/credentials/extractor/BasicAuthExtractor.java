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
package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * To extract basic auth header.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class BasicAuthExtractor implements Extractor<UsernamePasswordCredentials> {

    private final HeaderExtractor extractor;

    private final String clientName;

    public BasicAuthExtractor(final String clientName) {
        this(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BASIC_HEADER_PREFIX, clientName);
    }

    public BasicAuthExtractor(final String headerName, final String prefixHeader, final String clientName) {
        this.extractor = new HeaderExtractor(headerName, prefixHeader, clientName);
        this.clientName = clientName;
    }

    @Override
    public UsernamePasswordCredentials extract(WebContext context) {
        final TokenCredentials credentials = this.extractor.extract(context);
        if (credentials == null) {
            return null;
        }

        final byte[] decoded = Base64.getDecoder().decode(credentials.getToken());

        String token;
        try {
            token = new String(decoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new CredentialsException("Bad format of the basic auth header");
        }

        final int delim = token.indexOf(":");
        if (delim < 0) {
            throw new CredentialsException("Bad format of the basic auth header");
        }
        return new UsernamePasswordCredentials(token.substring(0, delim),
                token.substring(delim + 1), clientName);
    }
}
