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

import org.scribe.up.profile.dropbox.DropBoxAttributesDefinition;
import org.scribe.up.profile.facebook.FacebookAttributesDefinition;
import org.scribe.up.profile.github.GitHubAttributesDefinition;
import org.scribe.up.profile.google.Google2AttributesDefinition;
import org.scribe.up.profile.google.GoogleAttributesDefinition;
import org.scribe.up.profile.linkedin.LinkedInAttributesDefinition;
import org.scribe.up.profile.twitter.TwitterAttributesDefinition;
import org.scribe.up.profile.windowslive.WindowsLiveAttributesDefinition;
import org.scribe.up.profile.wordpress.WordPressAttributesDefinition;
import org.scribe.up.profile.yahoo.YahooAttributesDefinition;

/**
 * This class defines all the attributes definitions.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class AttributesDefinitions {
    
    public final static AttributesDefinition facebookDefinition = new FacebookAttributesDefinition();
    
    public final static AttributesDefinition githubDefinition = new GitHubAttributesDefinition();
    
    public final static AttributesDefinition googleDefinition = new GoogleAttributesDefinition();
    
    public final static AttributesDefinition google2Definition = new Google2AttributesDefinition();
    
    public final static AttributesDefinition linkedinDefinition = new LinkedInAttributesDefinition();
    
    public final static AttributesDefinition twitterDefinition = new TwitterAttributesDefinition();
    
    public final static AttributesDefinition yahooDefinition = new YahooAttributesDefinition();
    
    public final static AttributesDefinition windowsLiveDefinition = new WindowsLiveAttributesDefinition();
    
    public final static AttributesDefinition wordPressDefinition = new WordPressAttributesDefinition();
    
    public final static AttributesDefinition dropBoxDefinition = new DropBoxAttributesDefinition();
}
