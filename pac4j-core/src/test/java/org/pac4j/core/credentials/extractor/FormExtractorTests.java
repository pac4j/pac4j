package org.pac4j.core.credentials.extractor;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;

import static org.junit.Assert.assertTrue;

/**
 * This is {@link FormExtractorTests}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
public class FormExtractorTests {
    @Test
    public void testExtractionFromRequestBody() {
        val context = MockWebContext.create()
            .setRequestMethod("post")
            .addRequestParameter("username", "pac4j")
            .addRequestParameter("password", "pac4j");
        val extractor = new FormExtractor("username", "password");
        extractor.setExtractionMode(FormExtractor.ExtractionMode.REQUEST_BODY);
        assertTrue(extractor.extract(new CallContext(context, null)).isPresent());
    }

    @Test
    public void testExtractionFromQueryString() {
        val context = MockWebContext.create()
            .setRequestMethod("get")
            .addRequestParameter("username", "pac4j")
            .addRequestParameter("password", "pac4j")
            .setQueryString("username=pac4j&password=pac4j");
        val extractor = new FormExtractor("username", "password");
        extractor.setExtractionMode(FormExtractor.ExtractionMode.QUERY_PARAM);
        assertTrue(extractor.extract(new CallContext(context, null)).isPresent());
    }
}
