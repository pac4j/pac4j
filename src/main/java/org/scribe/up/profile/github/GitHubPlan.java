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
package org.scribe.up.profile.github;

import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a GitHub plan.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class GitHubPlan extends JsonObject {
    
    private static final long serialVersionUID = -4718500186419958716L;
    
    private String name;
    
    private Integer collaborators;
    
    private Integer space;
    
    private Integer privateRepos;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.name = Converters.stringConverter.convertFromJson(json, "name");
        this.collaborators = Converters.integerConverter.convertFromJson(json, "collaborators");
        this.space = Converters.integerConverter.convertFromJson(json, "space");
        this.privateRepos = Converters.integerConverter.convertFromJson(json, "private_repos");
    }
    
    public String getName() {
        return this.name;
    }
    
    public Integer getCollaborators() {
        return this.collaborators;
    }
    
    public Integer getSpace() {
        return this.space;
    }
    
    public Integer getPrivateRepos() {
        return this.privateRepos;
    }
}
