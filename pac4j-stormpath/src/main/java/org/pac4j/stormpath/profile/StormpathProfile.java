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
package org.pac4j.stormpath.profile;

import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.group.GroupMembershipList;
import org.pac4j.core.profile.CommonProfile;

/**
 * <p>The user profile returned from a Stormpath Authentication event.</p>
 * @author Misagh Moayyed
 * @since 1.8
 */
public class StormpathProfile extends CommonProfile {

    private static final long serialVersionUID = 7289249610131900281L;

    @Override
    public String getDisplayName() {
        return getAttribute("fullName").toString();
    }

    @Override
    public String getFamilyName() {
        return getAttribute("surName").toString();
    }

    @Override
    public String getFirstName() {
        return getAttribute("givenName").toString();
    }

    public String getMiddleName() {
        return getAttribute("middleName").toString();
    }

    public GroupList getGroups() {
        return getAttribute("middleName", GroupList.class);
    }

    public GroupMembershipList getGroupMemberships() {
        return getAttribute("groupMemberships", GroupMembershipList.class);
    }

    public AccountStatus getStatus() {
        return getAttribute("status", AccountStatus.class);
    }

}
