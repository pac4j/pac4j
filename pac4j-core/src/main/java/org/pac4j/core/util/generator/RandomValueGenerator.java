package org.pac4j.core.util.generator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

/**
 * Value generator which returns a random value.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class RandomValueGenerator implements ValueGenerator {

    private int size;

    public RandomValueGenerator() {
        setSize(10);
    }

    public RandomValueGenerator(final int size) {
        setSize(size);
    }

    @Override
    public String generateValue(final WebContext webContext) {
        return CommonHelper.randomString(size);
    }

    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        CommonHelper.assertTrue(size > 0, "size must be greater than 0");
        this.size = size;
    }
}
