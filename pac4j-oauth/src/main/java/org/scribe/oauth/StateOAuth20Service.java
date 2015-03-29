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
package org.scribe.oauth;

/**
 * This interface defines that a service can handle a state parameter for the authorization url.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public interface StateOAuth20Service {
    
    /**
     * Return an authorization url using a state parameter.
     * 
     * @param state a state parameter.
     * @return an authorization url with a state parameter.
     */
    public String getAuthorizationUrl(final String state);
}
