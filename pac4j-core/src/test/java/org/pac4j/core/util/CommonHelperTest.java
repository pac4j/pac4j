package org.pac4j.core.util;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;

public class CommonHelperTest {

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

}
