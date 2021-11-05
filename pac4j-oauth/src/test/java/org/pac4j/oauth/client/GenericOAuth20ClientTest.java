package org.pac4j.oauth.client;

import org.junit.Test;
import org.pac4j.core.profile.converter.BooleanConverter;
import org.pac4j.core.profile.converter.ColorConverter;
import org.pac4j.core.profile.converter.GenderConverter;
import org.pac4j.core.profile.converter.IntegerConverter;
import org.pac4j.core.profile.converter.LocaleConverter;
import org.pac4j.core.profile.converter.LongConverter;
import org.pac4j.core.profile.converter.UrlConverter;
import org.pac4j.oauth.profile.generic.GenericOAuth20ProfileDefinition;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.pac4j.core.util.TestsConstants.CALLBACK_URL;
import static org.pac4j.core.util.TestsConstants.ID;

public class GenericOAuth20ClientTest {
    final static String AGE = "age";
    final static String IS_ADMIN = "is_admin";
    final static String BG_COLOR = "bg_color";
    final static String GENDER = "gender";
    final static String BIRTHDAY = "birthday";
    final static String BLOG = "blog";

    @Test
    public void setProfileAttrs() throws Exception {
        var client = new GenericOAuth20Client();
        Map map = new HashMap();
        map.put(AGE, "Integer|age");
        //map.put("creation_time", "Date:|creation_time");
        map.put(IS_ADMIN, "Boolean|is_admin");
        map.put(BG_COLOR, "Color|bg_color");
        map.put(GENDER, "Gender|gender");
        map.put(BIRTHDAY, "Locale|birthday");
        map.put(ID, "Long|id");
        map.put(BLOG, "URI|blog");
        client.setProfileAttrs(map);
        client.setCallbackUrl(CALLBACK_URL);
        client.init();

        var profileDefinition = (GenericOAuth20ProfileDefinition) client.getConfiguration().getProfileDefinition();
        assertTrue(profileDefinition.getConverters().get(AGE) instanceof IntegerConverter);
        assertTrue(profileDefinition.getConverters().get(IS_ADMIN) instanceof BooleanConverter);
        assertTrue(profileDefinition.getConverters().get(BG_COLOR) instanceof ColorConverter);
        assertTrue(profileDefinition.getConverters().get(GENDER) instanceof GenderConverter);
        assertTrue(profileDefinition.getConverters().get(BIRTHDAY) instanceof LocaleConverter);
        assertTrue(profileDefinition.getConverters().get(ID) instanceof LongConverter);
        assertTrue(profileDefinition.getConverters().get(BLOG) instanceof UrlConverter);
    }

}
