package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.LinkedIn2Client;
import org.pac4j.oauth.profile.linkedin2.*;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link LinkedIn2Client}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunLinkedIn2Client extends RunClient {

    public static void main(String[] args) {
        new RunLinkedIn2Client().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup56";
    }

    @Override
    protected IndirectClient getClient() {
        final LinkedIn2Client client = new LinkedIn2Client();
        client.setKey("gsqj8dn56ayn");
        client.setSecret("kUFAZ2oYvwMQ6HFl");
        client.setScope("r_basicprofile r_emailaddress rw_company_admin w_share");
        client.setCallbackUrl(PAC4J_URL);
        return client;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final LinkedIn2Profile profile = (LinkedIn2Profile) userProfile;
        assertEquals("JJjS_5BOzW", profile.getId());
        assertEquals(LinkedIn2Profile.class.getName() + CommonProfile.SEPARATOR + "JJjS_5BOzW",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), LinkedIn2Profile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile,
                "testscribeup@gmail.com",
                "test",
                "scribeUp",
                "test scribeUp",
                null,
                Gender.UNSPECIFIED,
                null,
                "https://media.licdn.com/dms/image/C5603AQHfitHQ5I8fcg/profile-displayphoto-shrink_100_100/" +
                    "0?e=1527703200&v=alpha&t=6Vxlhr64CF5LP_O9_YJwQv8Ar_TZjnZ_4C53xoygjfI",
                "https://www.linkedin.com/in/test-scribeup-16b0aa48", "Paris Area, France");
        final LinkedIn2Location location = profile.getCompleteLocation();
        assertEquals("Paris Area, France", location.getName());
        assertNull(profile.getMaidenName());
        assertEquals("ScribeUP développeur chez OpenSource", profile.getHeadline());
        assertEquals("Information Technology and Services", profile.getIndustry());
        assertEquals(1, profile.getNumConnections().intValue());
        assertEquals("This is a summary...", profile.getSummary());
        assertNull(profile.getSpecialties());
        final List<LinkedIn2Position> positions = profile.getPositions();
        assertEquals(2, positions.size());
        final LinkedIn2Position position = positions.get(0);
        assertEquals("417494299", position.getId());
        assertEquals("Developer", position.getTitle());
        assertEquals("Desc", position.getSummary());
        final LinkedIn2Date startDate = position.getStartDate();
        assertEquals(2012, startDate.getYear().intValue());
        assertEquals(3, startDate.getMonth().intValue());
        assertNull(position.getEndDate());
        final LinkedIn2Company company = position.getCompany();
        assertEquals("PAC4J", company.getName());
        assertNull(company.getIndustry());
        assertEquals("https://www.linkedin.com/profile/view?id=AAoAAAn67mMBxVIxeJXn2T6XBvOFEAMLv7RiJQQ"
                + "&authType=name&authToken=_IWF&trk=api*a167383*s175634*",
                profile.getSiteStandardProfileRequest());
        assertEquals("AAoAAAn67mMBxVIxeJXn2T6XBvOFEAMLv7RiJQQ", profile.getOAuth10Id());
        assertEquals(16, profile.getAttributes().size());
    }
}
