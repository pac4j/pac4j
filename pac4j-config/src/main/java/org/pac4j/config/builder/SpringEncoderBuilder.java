package org.pac4j.config.builder;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.credentials.password.SpringSecurityPasswordEncoder;
import org.pac4j.core.exception.TechnicalException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder of Spring Crypto password encoder.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
@Slf4j
public class SpringEncoderBuilder extends AbstractBuilder {

    /**
     * <p>Constructor for SpringEncoderBuilder.</p>
     *
     * @param properties a {@link Map} object
     */
    public SpringEncoderBuilder(final Map<String, String> properties) {
        super(properties);
    }

    /**
     * <p>tryCreatePasswordEncoder.</p>
     *
     * @param encoders a {@link Map} object
     */
    public void tryCreatePasswordEncoder(final Map<String, org.pac4j.core.credentials.password.PasswordEncoder> encoders) {
        for (var i = 0; i <= MAX_NUM_ENCODERS; i++) {
            val type = getProperty(SPRING_ENCODER_TYPE, i);
            if (isNotBlank(type)) {
                final PasswordEncoder encoder;
                if (SpringEncoderType.NOOP.toString().equalsIgnoreCase(type)) {
                    LOGGER.debug("Please notice that the NOOP Spring encoder type is insecure and for tests only");
                    encoder = NoOpPasswordEncoder.getInstance();
                } else if (SpringEncoderType.BCRYPT.toString().equalsIgnoreCase(type)) {
                    if (containsProperty(SPRING_ENCODER_BCRYPT_LENGTH, i)) {
                        encoder = new BCryptPasswordEncoder(getPropertyAsInteger(SPRING_ENCODER_BCRYPT_LENGTH, i));
                    } else {
                        encoder = new BCryptPasswordEncoder();
                    }
                } else if (SpringEncoderType.PBKDF2.toString().equalsIgnoreCase(type)) {
                    if (containsProperty(SPRING_ENCODER_PBKDF2_SECRET, i)) {
                        val secret = getProperty(SPRING_ENCODER_PBKDF2_SECRET, i);
                        if (containsProperty(SPRING_ENCODER_PBKDF2_ITERATIONS, i)
                            && containsProperty(SPRING_ENCODER_PBKDF2_HASH_WIDTH, i)) {
                            encoder = new Pbkdf2PasswordEncoder(secret, 16, getPropertyAsInteger(SPRING_ENCODER_PBKDF2_ITERATIONS, i),
                                getPropertyAsInteger(SPRING_ENCODER_PBKDF2_HASH_WIDTH, i));
                        } else {
                            encoder = new Pbkdf2PasswordEncoder(secret, 16, 310000,
                                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
                        }
                    } else {
                        encoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
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
                        encoder = SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8();
                    }
                } else if (SpringEncoderType.STANDARD.toString().equalsIgnoreCase(type)) {
                    LOGGER.debug("Please notice that the STANDARD Spring encoder type is insecure and for tests only");
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
