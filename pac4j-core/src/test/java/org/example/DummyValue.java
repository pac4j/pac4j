package org.example;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class should be in a package different from org.pack4j.
 * This is needed to test untrusted package deserialization feature
 */
public class DummyValue implements Serializable {
    private String value;

    public DummyValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DummyValue that = (DummyValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
