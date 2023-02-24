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

    /**
     * <p>Constructor for StaticValueGenerator.</p>
     */
    public StaticValueGenerator() {}

    /**
     * <p>Constructor for StaticValueGenerator.</p>
     *
     * @param value a {@link java.lang.String} object
     */
    public StaticValueGenerator(final String value) {
        this.value = value;
    }

    /** {@inheritDoc} */
    @Override
    public String generateValue(final CallContext ctx) {
        CommonHelper.assertNotNull("value", value);
        return value;
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getValue() {
        return value;
    }

    /**
     * <p>Setter for the field <code>value</code>.</p>
     *
     * @param value a {@link java.lang.String} object
     */
    public void setValue(final String value) {
        this.value = value;
    }
}
