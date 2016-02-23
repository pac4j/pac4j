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
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;

/**
 * An extractor gets the {@link Credentials} from a {@link WebContext} and should return <code>null</code> if no credentials are present
 * or should throw a {@link CredentialsException} if it cannot get it.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface CredentialsExtractor<C extends Credentials> {

    /**
     * Extract the right credentials. It should throw a {@link CredentialsException} in case of failure.
     *
     * @param context the current web context
     * @return the credentials.
     */
    C extract(WebContext context);
}
