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

import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.binding.decoding.HTTPPostDecoder;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.transport.InTransport;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.pac4j.core.context.WebContext;

/**
 * Extends {@link HTTPPostDecoder} to override getActualReceiverEndpointURI() because we
 * do not have an {@link HttpServletRequestAdapter} as input in pac4j.
 * 
 * @author Michael Remond
 * @since 1.5.0
 *
 */
public class Pac4jHTTPPostDecoder extends HTTPPostDecoder {

    public Pac4jHTTPPostDecoder(final StaticBasicParserPool parserPool) {
        super(parserPool);
    }

    @Override
    protected String getActualReceiverEndpointURI(final SAMLMessageContext messageContext)
            throws MessageDecodingException {
        InTransport inTransport = messageContext.getInboundMessageTransport();
        if (!(inTransport instanceof SimpleRequestAdapter)) {
            throw new MessageDecodingException("Message context InTransport instance was an unsupported type");
        }
        WebContext webContext = ((SimpleRequestAdapter) inTransport).getWebContext();

        return webContext.getFullRequestURL();
    }

}
