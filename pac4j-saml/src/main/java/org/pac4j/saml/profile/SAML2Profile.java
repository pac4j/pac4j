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
package org.pac4j.saml.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.saml.client.SAML2Client;

/**
 * <p>This class is the user profile for sites using SAML2 protocol.</p>
 * <p>It is returned by the {@link SAML2Client}.</p>
 * 
 * @author Michael Remond
 * @author Misagh Moayyed
 * @version 1.5.0
 */
public class SAML2Profile extends CommonProfile {

    private static final long serialVersionUID = -7811733390277407623L;
}
