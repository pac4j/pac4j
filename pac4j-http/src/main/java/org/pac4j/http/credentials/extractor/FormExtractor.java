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
package org.pac4j.http.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.UsernamePasswordCredentials;

/**
 * To extract a username and password posted from a form.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class FormExtractor implements Extractor<UsernamePasswordCredentials> {

    private final String usernameParameter;

    private final String passwordParameter;

    private final String clientName;

    public FormExtractor(final String usernameParameter, final String passwordParameter, final String clientName) {
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
        this.clientName = clientName;
    }

    public UsernamePasswordCredentials extract(WebContext context) {
        final String username = context.getRequestParameter(this.usernameParameter);
        final String password = context.getRequestParameter(this.passwordParameter);
        if (username == null || password == null) {
            return null;
        }

        return new UsernamePasswordCredentials(username, password, clientName);
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }
}
