package org.pac4j.core.util;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * This class tests the {@link CommonHelper} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CommonHelperTests {

    private static final String URL_WITHOUT_PARAMETER = "http://host/app";

    private static final String URL_WITH_PARAMETER = "http://host/app?param=value";

    private static final String NAME = "name";

    private static final String VALUE = "va+l+ue";

    private static final String ENCODED_VALUE = "va%2Bl%2Bue";

    private static final Class<?> CLAZZ = String.class;

    private static final String CLASS_NAME = String.class.getSimpleName();

    @Test
    public void testIsNotBlankNull() {
        assertFalse(CommonHelper.isNotBlank(null));
    }

    @Test
    public void testIsNotBlankEmply() {
        assertFalse(CommonHelper.isNotBlank(""));
    }

    @Test
    public void testIsNotBlankBlank() {
        assertFalse(CommonHelper.isNotBlank("     "));
    }

    @Test
    public void testIsNotBlankNotBlank() {
        assertTrue(CommonHelper.isNotBlank(NAME));
    }

    @Test
    public void testAssertNotBlankBlank() {
        try {
            CommonHelper.assertNotBlank(NAME, "");
            fail("must throw an ClientException");
        } catch (final TechnicalException e) {
            assertEquals(NAME + " cannot be blank", e.getMessage());
        }
    }

    @Test
    public void testAssertNotBlankNotBlank() {
        CommonHelper.assertNotBlank(NAME, VALUE);
    }

    @Test
    public void testAssertNotNullNull() {
        try {
            CommonHelper.assertNotNull(NAME, null);
            fail("must throw an ClientException");
        } catch (final TechnicalException e) {
            assertEquals(NAME + " cannot be null", e.getMessage());
        }
    }

    @Test
    public void testAssertNotNullNotNull() {
        CommonHelper.assertNotNull(NAME, VALUE);
    }

    @Test
    public void testAddParameterNullUrl() {
        assertNull(CommonHelper.addParameter(null, NAME, VALUE));
    }

    @Test
    public void testAddParameterNullName() {
        assertEquals(URL_WITH_PARAMETER, CommonHelper.addParameter(URL_WITH_PARAMETER, null, VALUE));
    }

    @Test
    public void testAddParameterNullValue() {
        assertEquals(URL_WITH_PARAMETER + "&" + NAME + "=", CommonHelper.addParameter(URL_WITH_PARAMETER, NAME, null));
    }

    @Test
    public void testAddParameterWithParameter() {
        assertEquals(URL_WITH_PARAMETER + "&" + NAME + "=" + ENCODED_VALUE,
                CommonHelper.addParameter(URL_WITH_PARAMETER, NAME, VALUE));
    }

    @Test
    public void testAddParameterWithoutParameter() {
        assertEquals(URL_WITHOUT_PARAMETER + "?" + NAME + "=" + ENCODED_VALUE,
                CommonHelper.addParameter(URL_WITHOUT_PARAMETER, NAME, VALUE));
    }

    @Test
    public void testToStringNoParameter() {
        assertEquals("#" + CLASS_NAME + "# |", CommonHelper.toString(CLAZZ));
    }

    @Test
    public void testToStringWithParameter() {
        assertEquals("#" + CLASS_NAME + "# | " + NAME + ": " + VALUE + " |", CommonHelper.toString(CLAZZ, NAME, VALUE));
    }

    @Test
    public void testToStringWithParameters() {
        assertEquals("#" + CLASS_NAME + "# | " + NAME + ": " + VALUE + " | " + NAME + ": " + VALUE + " |",
                CommonHelper.toString(CLAZZ, NAME, VALUE, NAME, VALUE));
    }

    @Test
    public void testAreEqualsOk() {
        assertTrue(CommonHelper.areEquals(null, null));
        assertTrue(CommonHelper.areEquals(VALUE, VALUE));
    }

    @Test
    public void testAreEqualsIgnoreCaseAndTrimOk() {
        assertTrue(CommonHelper.areEqualsIgnoreCaseAndTrim(null, null));
        assertTrue(CommonHelper.areEqualsIgnoreCaseAndTrim(" " + VALUE.toUpperCase(), VALUE + "                "));
    }

    @Test
    public void testAreEqualsFails() {
        assertFalse(CommonHelper.areEquals(VALUE, null));
        assertFalse(CommonHelper.areEquals(null, VALUE));
        assertFalse(CommonHelper.areEquals(NAME, VALUE));
    }

    @Test
    public void testAreEqualsIgnoreCaseAndTrimFails() {
        assertFalse(CommonHelper.areEqualsIgnoreCaseAndTrim(VALUE, null));
        assertFalse(CommonHelper.areEqualsIgnoreCaseAndTrim(NAME, VALUE));
    }
    
    @Test
    public void testNoPrefix() {
    	assertNotNull(CommonHelper.getInputStreamFromName("src/test/resources/testFile.txt"));
    }

    @Test
    public void testFilePrefix() {
        assertNotNull(CommonHelper.getInputStreamFromName("file:src/test/resources/testFile.txt"));
    }

    @Test
    public void testResourcePrefix() {
    	assertNotNull(CommonHelper.getInputStreamFromName("resource:/testFile.txt"));
    }
    
    @Test
    public void testClassPathPrefix() {
    	assertNotNull(CommonHelper.getInputStreamFromName("classpath:testFile.txt"));
    }
    
    @Test
    public void testHttpPrefix() {
    	assertNotNull(CommonHelper.getInputStreamFromName("http://www.yahoo.com"));
    }
    
    @Test
    public void testHttpsPrefix() {
    	assertNotNull(CommonHelper.getInputStreamFromName("https://www.yahoo.com"));
    }

    @Test
    public void testWindowsPath() {
        final String file = "C:\\XYZ\\idp.xml";
        TestsHelper.expectException(() -> CommonHelper.getInputStreamFromName(file), TechnicalException.class, "prefix is not handled:C");
    }

    @Test(expected = TechnicalException.class)
    public void testAssertNotBlank_null() {
        String var = null;
        CommonHelper.assertNotBlank("var", var);
    }
  
    @Test(expected = TechnicalException.class)
    public void testAssertNotBlank_empty() {
        String var = " ";
        CommonHelper.assertNotBlank("var", var);
    }

    @Test
    public void testAssertNotBlank_notBlank() {
        String var = "contents";
        CommonHelper.assertNotBlank("var", var);
    }

    @Test(expected = TechnicalException.class)
    public void testAssertNotNull_null() {
        String var = null;
        CommonHelper.assertNotNull("var", var);
    }

    @Test
    public void testAssertNotNull_notBlank() {
        String var = "contents";
        CommonHelper.assertNotNull("var", var);
    }

    @Test
    public void testAssertNull_null() {
        CommonHelper.assertNull("var", null);
    }

    @Test(expected = TechnicalException.class)
    public void testAssertNull_notNull() {
        CommonHelper.assertNull("var", "notnull");
    }

    @Test
    public void testRandomString() {
        assertNotNull(CommonHelper.randomString(10));
    }

    @Test
    public void testSubstringAfter() {
        assertEquals("after", CommonHelper.substringAfter("before###after", "###"));
    }

    @Test
    public void testSubstringBefore() {
        assertEquals("before", CommonHelper.substringBefore("before###after", "###"));
    }

    @Test
    public void testSubstringBetween() {
        assertEquals("bet", CommonHelper.substringBetween("123startbet##456", "start", "##"));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(CommonHelper.isEmpty(null));
        assertTrue(CommonHelper.isEmpty(new ArrayList<>()));
        assertFalse(CommonHelper.isEmpty(Arrays.asList(new String[] { VALUE })));
    }

    @Test
    public void testIsNotEmpty() {
        assertFalse(CommonHelper.isNotEmpty(null));
        assertFalse(CommonHelper.isNotEmpty(new ArrayList<>()));
        assertTrue(CommonHelper.isNotEmpty(Arrays.asList(new String[] { VALUE })));
    }
}
