/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.profile.facebook;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents a Facebook application.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookApplication extends FacebookObject {
    
    private static final long serialVersionUID = -2704985540417732739L;
    
    private String namespace;
    
    public FacebookApplication(final Object json) {
        super(json);
    }
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        super.buildFromJson(json);
        this.namespace = Converters.stringConverter.convertFromJson(json, "namespace");
    }
    
    public String getNamespace() {
        return namespace;
    }
}
