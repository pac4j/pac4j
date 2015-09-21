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
package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;

/**
 * <p>This class is the default direct (stateless) implementation of an authentication client (whatever the protocol).
 * In that case, redirecting does not have any sense.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public abstract class DirectClient<C extends Credentials, U extends CommonProfile> extends BaseClient<C, U> {

    @Override
    public final void redirect(final WebContext context, final boolean protectedTarget) {
        throw new TechnicalException("direct clients do not support redirections");
    }
}
