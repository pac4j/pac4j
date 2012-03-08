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

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.util.ObjectHelper;

/**
 * This class represents a GitHub plan.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class GitHubPlan {
    
    private String name;
    
    private int collaborators;
    
    private int space;
    
    private int private_repos;
    
    public GitHubPlan(JsonNode json) {
        if (json != null) {
            this.name = JsonHelper.getTextValue(json, "name");
            this.collaborators = (Integer) ObjectHelper.getDefaultIfNull(JsonHelper.getNumberValue(json,
                                                                                                   "collaborators"), 0);
            this.space = (Integer) ObjectHelper.getDefaultIfNull(JsonHelper.getNumberValue(json, "space"), 0);
            this.private_repos = (Integer) ObjectHelper.getDefaultIfNull(JsonHelper.getNumberValue(json,
                                                                                                   "private_repos"), 0);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public int getCollaborators() {
        return collaborators;
    }
    
    public int getSpace() {
        return space;
    }
    
    public int getPrivateRepos() {
        return private_repos;
    }
    
    @Override
    public String toString() {
        return "GitHubPlan(name:" + name + ",collaborators:" + collaborators + ",space:" + space + ",private_repos:"
               + private_repos + ")";
    }
}
