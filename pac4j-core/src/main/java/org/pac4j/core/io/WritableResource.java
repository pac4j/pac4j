package org.pac4j.core.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Extended interface for a resource that supports writing to it. Provides an
 * {@link #getOutputStream() OutputStream accessor}.
 * This is based originally off spring abstraction.
 * 
 *  @author Keith Garry Boyce
 *  @since 1.9.0
 *
 */
public interface WritableResource extends Resource {


	/**
	 * Return an {@link OutputStream} for the underlying resource, allowing to
	 * (over-)write its content.
	 * 
	 * Caller is responsible for closing the stream
	 * 
	 * @throws IOException
	 *             if the stream could not be opened
	 * @return the output stream for the underlying resource (must not be {@code null})
	 * @see #getInputStream()
	 */
	OutputStream getOutputStream() throws IOException;

}
