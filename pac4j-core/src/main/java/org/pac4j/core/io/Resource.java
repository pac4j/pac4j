package org.pac4j.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for a resource descriptor that abstracts from the actual
 * type of underlying resource, such as a file or class path resource.
 *
 * <p>An InputStream can be opened for every resource if it exists in
 * physical form, but a URL or File handle can just be returned for
 * certain resources. The actual behavior is implementation-specific.
 * 
 *  @since 1.9.0
 *
 */
public interface Resource {

	/**
	 * Return whether this resource actually exists in physical form.
	 * <p>This method performs a definitive existence check, whereas the
	 * existence of a {@code Resource} handle only guarantees a
	 * valid descriptor handle.
	 * @return whether this resource actually exists in physical form
	 */
	boolean exists();

	/**
	 * Determine a filename for this resource, i.e. typically the last
	 * part of the path: for example, "myfile.txt".
	 * <p>Returns {@code null} if this type of resource does not
	 * have a filename.
	 * @return a filename for this resource, i.e. typically the last
	 */
	String getFilename();
	
	/**
	 * Return an {@link InputStream}.
	 * 
	 * Caller is responsible for closing the stream
	 * 
	 * @return the input stream for the underlying resource (must not be {@code null})
	 * @throws IOException if the stream could not be opened
	 */
	InputStream getInputStream() throws IOException;


}
