package org.pac4j.core.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Extended interface for a resource that supports writing to it. Provides an
 * {@link #getOutputStream() OutputStream accessor}.
 * 
 *  @since 1.9.0
 *
 */
public interface WritableResource extends Resource {


	/**
	 * Return an {@link OutputStream} for the underlying resource, allowing to
	 * (over-)write its content.
	 * 
	 * @throws IOException
	 *             if the stream could not be opened
	 * @see #getInputStream()
	 */
	OutputStream getOutputStream() throws IOException;

}
