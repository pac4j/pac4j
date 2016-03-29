package org.pac4j.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple interface for objects that are sources for an {@link InputStream}.
 *
 * <p>This is the base interface for @link Resource} interface.
 *
 * @see java.io.InputStream
 * @see Resource
 *  @since 1.9.0
 */
public interface InputStreamSource {

	/**
	 * Return an {@link InputStream}.
	 * 
	 * @return the input stream for the underlying resource (must not be {@code null})
	 * @throws IOException if the stream could not be opened
	 */
	InputStream getInputStream() throws IOException;

}
