package org.pac4j.oauth.client;

import org.junit.Test;
import org.pac4j.core.profile.converter.*;
import org.pac4j.core.profile.definition.ProfileDefinition;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.generic.GenericOAuth20ProfileDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.pac4j.core.util.TestsConstants.CALLBACK_URL;
import static org.pac4j.core.util.TestsConstants.ID;

/**
 * Created by Administrator on 2018/1/2.
 */
public class GenericOAuth20ClientTest {
    final String AGE = "age";
    final String IS_ADMIN = "is_admin";
    final String BG_COLOR = "bg_color";
    final String GENDER = "gender";
    final String BIRTHDAY = "birthday";
    final String BLOG = "blog";

    @Test
    public void setProfileAttrs() throws Exception {
        GenericOAuth20Client client = new GenericOAuth20Client();
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
        Field configurationField = OAuth20Client.class.getDeclaredField("configuration");
        configurationField.setAccessible(true);
        OAuth20Configuration configuration = (OAuth20Configuration) configurationField.get(client);
        GenericOAuth20ProfileDefinition profileDefinition = (GenericOAuth20ProfileDefinition) configuration.getProfileDefinition();
        Method getConverters = ProfileDefinition.class.getDeclaredMethod("getConverters");
        getConverters.setAccessible(true);
        Map<String, AttributeConverter<? extends Object>> converters = (Map<String, AttributeConverter<? extends Object>>) getConverters.invoke(profileDefinition);
        assertTrue(converters.get(AGE) instanceof IntegerConverter);
        assertTrue(converters.get(IS_ADMIN) instanceof BooleanConverter);
        assertTrue(converters.get(BG_COLOR) instanceof ColorConverter);
        assertTrue(converters.get(GENDER) instanceof GenderConverter);
        assertTrue(converters.get(BIRTHDAY) instanceof LocaleConverter);
        assertTrue(converters.get(ID) instanceof LongConverter);
        assertTrue(converters.get(BLOG) instanceof UrlConverter);
    }

}