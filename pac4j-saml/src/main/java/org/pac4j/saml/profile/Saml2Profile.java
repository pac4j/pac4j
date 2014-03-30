/*
  Copyright 2012 -2014 Michael Remond

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
package org.pac4j.saml.profile;

import org.pac4j.core.profile.CommonProfile;

/**
 * This class is the user profile for sites using SAML2 protocol.<br />
 * It is returned by the {@link org.pac4j.cas.client.Saml2Client}.
 * 
 * @author Michael Remond
 * @version 1.5.0
 */
public class Saml2Profile extends CommonProfile {

    private static final long serialVersionUID = -7811733390277407623L;
}
