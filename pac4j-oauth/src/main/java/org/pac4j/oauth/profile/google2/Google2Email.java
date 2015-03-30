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
package org.pac4j.oauth.profile.google2;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents an email object for Google.
 *
 * @author Nate Williams
 * @since 1.6.1
 */
public final class Google2Email extends JsonObject {

    private static final long serialVersionUID = 3273984944635729083L;

    private String email;
    private String type;

    @Override
    protected void buildFromJson(final JsonNode json) {
        this.email = (String) JsonHelper.convert(Converters.stringConverter, json, "value");
        this.type = (String) JsonHelper.convert(Converters.stringConverter, json, "type");
    }

    public String getEmail() {
        return this.email;
    }

    public String getType() {
        return this.type;
    }
}
