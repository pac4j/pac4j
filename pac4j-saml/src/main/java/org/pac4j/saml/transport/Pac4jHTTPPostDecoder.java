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

import net.shibboleth.utilities.java.support.xml.ParserPool;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.saml1.binding.decoding.impl.HTTPPostDecoder;
import org.opensaml.saml.saml2.metadata.Endpoint;
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

    public Pac4jHTTPPostDecoder(final ParserPool parserPool) {
        super();
    }

    @Override
    public void setMessageContext(MessageContext<SAMLObject> context) {
        super.setMessageContext(context);
    }

    @Override
    protected void populateBindingContext(MessageContext<SAMLObject> messageContext) {
        super.populateBindingContext(messageContext);

        InOutOperationContext ctx = messageContext.getSubcontext(InOutOperationContext.class);
        if (!(ctx.getInboundMessageContext() instanceof SimpleRequestAdapter)) {
            throw new RuntimeException("Message context InTransport instance was an unsupported type");
        }
        WebContext webContext = ((SimpleRequestAdapter) ctx.getInboundMessageContext()).getWebContext();
        Endpoint intendedEndpoint = messageContext.getSubcontext(SAMLEndpointContext.class).getEndpoint();
        intendedEndpoint.setLocation(webContext.getFullRequestURL());

    }
}
