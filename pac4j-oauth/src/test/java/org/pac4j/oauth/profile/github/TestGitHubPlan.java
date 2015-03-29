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
package org.pac4j.oauth.profile.github;

import junit.framework.TestCase;

import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.JsonHelper;

/**
 * This class tests the {@link GitHubPlan} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestGitHubPlan extends TestCase implements TestsConstants {
    
    private static final int COLLABORATORS = 12;
    
    private static final int SPACE = 1545542;
    
    private static final int PRIVATE_REPOS = 6;
    
    private static final String GOOD_JSON = "{\"name\" : \"" + NAME + "\", \"collaborators\" : " + COLLABORATORS
                                            + ", \"space\" : " + SPACE + ", \"private_repos\" : " + PRIVATE_REPOS + "}";
    
    public void testNull() {
        final GitHubPlan gitHubPlan = new GitHubPlan();
        gitHubPlan.buildFrom(null);
        assertNull(gitHubPlan.getName());
        assertNull(gitHubPlan.getCollaborators());
        assertNull(gitHubPlan.getSpace());
        assertNull(gitHubPlan.getPrivateRepos());
    }
    
    public void testBadJson() {
        final GitHubPlan gitHubPlan = new GitHubPlan();
        gitHubPlan.buildFrom(JsonHelper.getFirstNode(BAD_JSON));
        assertNull(gitHubPlan.getName());
        assertNull(gitHubPlan.getCollaborators());
        assertNull(gitHubPlan.getSpace());
        assertNull(gitHubPlan.getPrivateRepos());
    }
    
    public void testGoodJson() {
        final GitHubPlan gitHubPlan = new GitHubPlan();
        gitHubPlan.buildFrom(JsonHelper.getFirstNode(GOOD_JSON));
        assertEquals(NAME, gitHubPlan.getName());
        assertEquals(COLLABORATORS, gitHubPlan.getCollaborators().intValue());
        assertEquals(SPACE, gitHubPlan.getSpace().intValue());
        assertEquals(PRIVATE_REPOS, gitHubPlan.getPrivateRepos().intValue());
    }
}
