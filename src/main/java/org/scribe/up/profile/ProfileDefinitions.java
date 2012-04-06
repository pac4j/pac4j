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
package org.scribe.up.profile;

import org.scribe.up.profile.facebook.FacebookProfileDefinition;
import org.scribe.up.profile.github.GitHubProfileDefinition;
import org.scribe.up.profile.google.GoogleProfileDefinition;
import org.scribe.up.profile.linkedin.LinkedInProfileDefinition;
import org.scribe.up.profile.twitter.TwitterProfileDefinition;
import org.scribe.up.profile.yahoo.YahooProfileDefinition;

/**
 * This class defines all the profile attributes definition.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class ProfileDefinitions {
    
    public final static AttributesDefinition facebookDefinition = new FacebookProfileDefinition();
    
    public final static AttributesDefinition githubDefinition = new GitHubProfileDefinition();
    
    public final static AttributesDefinition googleDefinition = new GoogleProfileDefinition();
    
    public final static AttributesDefinition linkedinDefinition = new LinkedInProfileDefinition();
    
    public final static AttributesDefinition twitterDefinition = new TwitterProfileDefinition();
    
    public final static AttributesDefinition yahooDefinition = new YahooProfileDefinition();
}
