package org.pac4j.core.context;

import lombok.val;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class WebContextTests {

    @Test
    public void testContextAttributes() {
        val context = MockWebContext.create();
        val attribute = Map.of("Hello", "World");
        context.setRequestAttribute("pac4j", attribute);
        assertTrue(context.getRequestAttribute("pac4j", Map.class).isPresent());
        assertThrows(ClassCastException.class, () -> context.getRequestAttribute("pac4j", List.class).isPresent());
    }
}
