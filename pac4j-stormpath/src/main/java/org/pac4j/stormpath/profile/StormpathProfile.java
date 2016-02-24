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
        return (String) getAttribute("fullName");
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute("surName");
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute("givenName");
    }

    public String getMiddleName() {
        return (String) getAttribute("middleName");
    }

    public GroupList getGroups() {
        return getAttribute("groups", GroupList.class);
    }

    public GroupMembershipList getGroupMemberships() {
        return getAttribute("groupMemberships", GroupMembershipList.class);
    }

    public AccountStatus getStatus() {
        return getAttribute("status", AccountStatus.class);
    }
}
