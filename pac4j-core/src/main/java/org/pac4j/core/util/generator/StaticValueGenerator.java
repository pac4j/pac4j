package org.pac4j.core.util.generator;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.util.CommonHelper;

/**
 * Value generator which returns a pre-defined value.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class StaticValueGenerator implements ValueGenerator {

    private String value;

    public StaticValueGenerator() {}

    public StaticValueGenerator(final String value) {
        this.value = value;
    }

    @Override
    public String generateValue(final CallContext ctx) {
        CommonHelper.assertNotNull("value", value);
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
