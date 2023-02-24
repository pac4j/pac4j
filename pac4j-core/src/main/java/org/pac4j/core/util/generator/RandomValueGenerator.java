package org.pac4j.core.util.generator;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.util.CommonHelper;

/**
 * Value generator which returns a random value.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class RandomValueGenerator implements ValueGenerator {

    private int size;

    /**
     * <p>Constructor for RandomValueGenerator.</p>
     */
    public RandomValueGenerator() {
        setSize(10);
    }

    /**
     * <p>Constructor for RandomValueGenerator.</p>
     *
     * @param size a int
     */
    public RandomValueGenerator(final int size) {
        setSize(size);
    }

    /** {@inheritDoc} */
    @Override
    public String generateValue(final CallContext ctx) {
        return CommonHelper.randomString(size);
    }

    /**
     * <p>Getter for the field <code>size</code>.</p>
     *
     * @return a int
     */
    public int getSize() {
        return size;
    }

    /**
     * <p>Setter for the field <code>size</code>.</p>
     *
     * @param size a int
     */
    public void setSize(final int size) {
        CommonHelper.assertTrue(size > 0, "size must be greater than 0");
        this.size = size;
    }
}
