package org.pac4j.core.util;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;

/**
 * Helper for files.
 *
 * @author Jerome LELEU
 * @since 6.5.0
 */
@Slf4j
public class FileHelper {

    public static void savePrivateFile(final Path target, final String content) throws IOException {
        try {
            val permissions = PosixFilePermissions.fromString("rw-------");
            val attributes = PosixFilePermissions.asFileAttribute(permissions);
            if (!Files.exists(target)) {
                Files.createFile(target, attributes);
            }
            Files.writeString(target, content, StandardOpenOption.TRUNCATE_EXISTING);
            Files.setPosixFilePermissions(target, permissions);
        } catch (final UnsupportedOperationException e) {
            LOGGER.warn("POSIX file permissions are not supported for path: {}", target);
            Files.writeString(target, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
