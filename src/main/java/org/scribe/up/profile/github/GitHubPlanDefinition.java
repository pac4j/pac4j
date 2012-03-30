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

import org.scribe.up.profile.AttributesDefinition;

/**
 * This class defines the GitHub plan object.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubPlanDefinition extends AttributesDefinition {
    
    public final static String NAME = "name";
    public final static String COLLABORATORS = "collaborators";
    public final static String SPACE = "space";
    public final static String PRIVATE_REPOS = "private_repos";
    
    public GitHubPlanDefinition() {
        attributes.add(NAME);
        converters.put(NAME, stringConverter);
        attributes.add(COLLABORATORS);
        converters.put(COLLABORATORS, integerConverter);
        attributes.add(SPACE);
        converters.put(SPACE, integerConverter);
        attributes.add(PRIVATE_REPOS);
        converters.put(PRIVATE_REPOS, integerConverter);
    }
}
