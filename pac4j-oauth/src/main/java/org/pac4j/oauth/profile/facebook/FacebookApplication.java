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
package org.pac4j.oauth.profile.facebook;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a Facebook application.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookApplication extends FacebookObject {
    
    private static final long serialVersionUID = 8888597071833762957L;
    
    private String namespace;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        super.buildFromJson(json);
        this.namespace = (String) JsonHelper.convert(Converters.stringConverter, json, "namespace");
    }
    
    @Override
    protected boolean isRootObject() {
        return false;
    }
    
    public String getNamespace() {
        return this.namespace;
    }
}
