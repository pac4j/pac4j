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
import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.JsonObject;

/**
 * This class represents a GitHub plan.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class GitHubPlan extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = 5048074846912710504L;
    
    private transient final static AttributesDefinition definition = new GitHubPlanDefinition();
    
    private String name;
    
    private Integer collaborators;
    
    private Integer space;
    
    private Integer private_repos;
    
    public GitHubPlan(Object json) {
        super(json);
    }
    
    @Override
    protected void buildFromJson(JsonNode json) {
        this.name = (String) definition.convert(json, GitHubPlanDefinition.NAME);
        this.collaborators = (Integer) definition.convert(json, GitHubPlanDefinition.COLLABORATORS);
        this.space = (Integer) definition.convert(json, GitHubPlanDefinition.SPACE);
        this.private_repos = (Integer) definition.convert(json, GitHubPlanDefinition.PRIVATE_REPOS);
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
        return private_repos != null ? private_repos : 0;
    }
    
    public boolean isPrivateReposDefined() {
        return private_repos != null;
    }
}
