package org.pac4j.core.credentials.password;

import com.google.common.collect.Streams;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

/**
 * Nop password encoder with constant time String compare. This should only be used in PoC implementations or where the password is already
 * encoded by something else.
 *
 * @author Timur Duehr
 * @since 3.1.0
 */
public class NopPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(String password) {
        return password;
    }

    @Override
    public boolean matches(String plainPassword, String encodedPassword) {
        if (plainPassword == null || encodedPassword == null || plainPassword.length() != encodedPassword.length()) {
            return false;
        }

        return (boolean) Streams.zip(Stream.of(plainPassword.getBytes(StandardCharsets.UTF_8)),
            Stream.of(encodedPassword.getBytes(StandardCharsets.UTF_8)), (plain, enc) -> {return plain == enc;})
            .reduce(true, (i, v) -> {return i == v;});
    }
}
