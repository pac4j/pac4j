/*
 *    Copyright 2012 - 2015 pac4j organization
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.pac4j.http.credentials;

import org.pac4j.core.util.CommonHelper;

/**
 * @author Misagh Moayyed
 * @since 1.8.1
 */
public class CookieCredentials extends HttpCredentials {

    private static final long serialVersionUID = -4270713434364817595L;

    private String name;
    private String value;

    public CookieCredentials(final String name, final String value, final String clientName) {
        this.name = name;
        this.value = value;
        setClientName(clientName);
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", this.name,
                "value", this.value, "clientName", getClientName());
    }

    @Override
    public void clear() {
        this.name = null;
        this.value = null;
        this.setClientName(null);
        this.setUserProfile(null);
    }
}
