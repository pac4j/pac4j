package org.pac4j.core.util;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;

public class CommonHelperTest {

    @Test
    public void testNoPrefix() {
    	CommonHelper.assertNotNull("var", CommonHelper.getInputStreamFromName("src/test/resources/testFile.txt"));
    }
    
    @Test
    public void testResourcePrefix() {
    	CommonHelper.assertNotNull("var", CommonHelper.getInputStreamFromName("resource:/testFile.txt"));
    }
    
    @Test
    public void testClassPathPrefix() {
    	CommonHelper.assertNotNull("var", CommonHelper.getInputStreamFromName("classpath:testFile.txt"));
    }
    
    @Test
    public void testHttpPrefix() {
    	CommonHelper.assertNotNull("var", CommonHelper.getInputStreamFromName("http://cnn.com"));
    }
    
    @Test
    public void testHttpsPrefix() {
    	CommonHelper.assertNotNull("var", CommonHelper.getInputStreamFromName("https://facebook.com"));
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
}
