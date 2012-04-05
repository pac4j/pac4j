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

import java.io.Serializable;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents a GitHub plan.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class GitHubPlan extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = -7255094058683970785L;
    
    private String name;
    
    private Integer collaborators;
    
    private Integer space;
    
    private Integer privateRepos;
    
    public GitHubPlan(Object json) {
        super(json);
    }
    
    @Override
    protected void buildFromJson(JsonNode json) {
        this.name = Converters.stringConverter.convertFromJson(json, "name");
        this.collaborators = Converters.integerConverter.convertFromJson(json, "collaborators");
        this.space = Converters.integerConverter.convertFromJson(json, "space");
        this.privateRepos = Converters.integerConverter.convertFromJson(json, "private_repos");
    }
    
    public String getName() {
        return name;
    }
    
    public int getCollaborators() {
        return collaborators != null ? collaborators : 0;
    }
    
    public boolean isCollaboratorsDefined() {
        return collaborators != null;
    }
    
    public int getSpace() {
        return space != null ? space : 0;
    }
    
    public boolean isSpaceDefined() {
        return space != null;
    }
    
    public int getPrivateRepos() {
        return privateRepos != null ? privateRepos : 0;
    }
    
    public boolean isPrivateReposDefined() {
        return privateRepos != null;
    }
}
