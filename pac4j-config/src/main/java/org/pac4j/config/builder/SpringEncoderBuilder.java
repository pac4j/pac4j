package org.pac4j.config.builder;

import org.pac4j.core.credentials.password.SpringSecurityPasswordEncoder;
import org.pac4j.core.exception.TechnicalException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.Map;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * Builder of Spring Crypto password encoder.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
public class SpringEncoderBuilder extends AbstractBuilder {

    public SpringEncoderBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryCreatePasswordEncoder(final Map<String, org.pac4j.core.credentials.password.PasswordEncoder> encoders) {
        for (int i = 0; i <= MAX_NUM_ENCODERS; i++) {
            final String type = getProperty(SPRING_ENCODER_TYPE, i);
            if (isNotBlank(type)) {
                final PasswordEncoder encoder;
                if (SpringEncoderType.NOOP.toString().equalsIgnoreCase(type)) {
                    encoder = NoOpPasswordEncoder.getInstance();
                } else if (SpringEncoderType.BCRYPT.toString().equalsIgnoreCase(type)) {
                    if (containsProperty(SPRING_ENCODER_BCRYPT_LENGTH, i)) {
                        encoder = new BCryptPasswordEncoder(getPropertyAsInteger(SPRING_ENCODER_BCRYPT_LENGTH, i));
                    } else {
                        encoder = new BCryptPasswordEncoder();
                    }
                } else if (SpringEncoderType.PBKDF2.toString().equalsIgnoreCase(type)) {
                    if (containsProperty(SPRING_ENCODER_PBKDF2_SECRET, i)) {
                        final String secret = getProperty(SPRING_ENCODER_PBKDF2_SECRET, i);
                        if (containsProperty(SPRING_ENCODER_PBKDF2_ITERATIONS, i)
                            && containsProperty(SPRING_ENCODER_PBKDF2_HASH_WIDTH, i)) {
                            encoder = new Pbkdf2PasswordEncoder(secret, getPropertyAsInteger(SPRING_ENCODER_PBKDF2_ITERATIONS, i),
                                getPropertyAsInteger(SPRING_ENCODER_PBKDF2_HASH_WIDTH, i));
                        } else {
                            encoder = new Pbkdf2PasswordEncoder(secret);
                        }
                    } else {
                        encoder = new Pbkdf2PasswordEncoder();
                    }
                } else if (SpringEncoderType.SCRYPT.toString().equalsIgnoreCase(type)) {
                    if (containsProperty(SPRING_ENCODER_SCRYPT_CPU_COST, i) && containsProperty(SPRING_ENCODER_SCRYPT_MEMORY_COST, i)
                        && containsProperty(SPRING_ENCODER_SCRYPT_PARALLELIZATION, i)
                        && containsProperty(SPRING_ENCODER_SCRYPT_KEY_LENGTH, i)
                        && containsProperty(SPRING_ENCODER_SCRYPT_SALT_LENGTH, i)) {
                        encoder = new SCryptPasswordEncoder(getPropertyAsInteger(SPRING_ENCODER_SCRYPT_CPU_COST, i),
                            getPropertyAsInteger(SPRING_ENCODER_SCRYPT_MEMORY_COST, i),
                                getPropertyAsInteger(SPRING_ENCODER_SCRYPT_PARALLELIZATION, i),
                            getPropertyAsInteger(SPRING_ENCODER_SCRYPT_KEY_LENGTH, i),
                                getPropertyAsInteger(SPRING_ENCODER_SCRYPT_SALT_LENGTH, i));
                    } else {
                        encoder = new SCryptPasswordEncoder();
                    }
                } else if (SpringEncoderType.STANDARD.toString().equalsIgnoreCase(type)) {
                    if (containsProperty(SPRING_ENCODER_STANDARD_SECRET, i)) {
                        encoder = new StandardPasswordEncoder(getProperty(SPRING_ENCODER_STANDARD_SECRET, i));
                    } else {
                        encoder = new StandardPasswordEncoder();
                    }
                } else {
                    throw new TechnicalException("Unsupported spring encoder type: " + type);
                }
                encoders.put(concat(SPRING_ENCODER, i), new SpringSecurityPasswordEncoder(encoder));
            }
        }
    }
}
