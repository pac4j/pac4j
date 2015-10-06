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
package org.pac4j.ldap.profile;

import org.pac4j.http.profile.HttpProfile;


/**
 * <p>The user profile returned by a LDAP authentication.</p>
 *
 * @see org.pac4j.ldap.credentials.authenticator.LdapAuthenticator
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class LdapProfile extends HttpProfile {

	private static final long serialVersionUID = 4745130273071234466L;	
}
