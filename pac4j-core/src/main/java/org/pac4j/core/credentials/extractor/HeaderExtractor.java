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

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.credentials.TokenCredentials;

/**
 * To extract header value.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class HeaderExtractor implements CredentialsExtractor<TokenCredentials> {

    private final String headerName;

    private final String prefixHeader;

    private final String clientName;

    public HeaderExtractor(final String headerName, final String prefixHeader, final String clientName) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
        this.clientName = clientName;
    }

    @Override
    public TokenCredentials extract(WebContext context) {
        final String header = context.getRequestHeader(this.headerName);
        if (header == null) {
            return null;
        }

        if  (!header.startsWith(this.prefixHeader)) {
            throw new CredentialsException("Wrong prefix for header: " + this.headerName);
        }

        final String headerWithoutPrefix = header.substring(this.prefixHeader.length());
        return new TokenCredentials(headerWithoutPrefix, clientName);
    }
}
