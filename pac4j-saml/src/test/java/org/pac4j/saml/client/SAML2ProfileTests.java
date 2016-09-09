package org.pac4j.saml.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.Gender;
import org.pac4j.saml.profile.SAML2Profile;

/**
 * Generic test for {@link SAML2Profile}
 * 
 * @author Ruochao Zheng
 * @since 1.9.2
 */
public class SAML2ProfileTests {

    @Test
    public void testAttributes() {
        // Create a profile
        SAML2Profile profile = new SAML2Profile();
        profile.addAttribute("email", wrapValue("rick@gmail.com"));
        profile.addAttribute("first_name", wrapValue("Rick"));
        profile.addAttribute("family_name", wrapValue("Zheng"));
        profile.addAttribute("display_name", wrapValue("Rick Zheng"));
        profile.addAttribute(Pac4jConstants.USERNAME, wrapValue("rick"));
        profile.addAttribute("gender", wrapValue("male"));
        profile.addAttribute("locale", wrapValue("en"));
        profile.addAttribute("picture_url", wrapValue("http://picture.com"));
        profile.addAttribute("profile_url", wrapValue("http://profile.com"));
        profile.addAttribute("location", wrapValue("San Francisco"));

        // Check the getter
        Assert.assertEquals("rick@gmail.com", profile.getEmail());
        Assert.assertEquals("Rick", profile.getFirstName());
        Assert.assertEquals("Zheng", profile.getFamilyName());
        Assert.assertEquals("Rick Zheng", profile.getDisplayName());
        Assert.assertEquals("rick", profile.getUsername());
        Assert.assertEquals(Gender.UNSPECIFIED, profile.getGender());
        Assert.assertEquals(null, profile.getLocale());
        Assert.assertEquals("http://picture.com", profile.getPictureUrl());
        Assert.assertEquals("http://profile.com", profile.getProfileUrl());
        Assert.assertEquals("San Francisco", profile.getLocation());
    }

    private List<String> wrapValue(String value) {
        List<String> list = new ArrayList<>();
        list.add(value);
        return list;
    }

}
