package org.pac4j.core.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A file path to resource wrapper so old file path code can use new abstraction. Provides an
 * {@link #getOutputStream() OutputStream accessor}.
 * 
 *  @since 1.9.0
 *
 */
public class FileSystemResource implements WritableResource {

	private String path;

	public FileSystemResource(String path) {
		this.path = path;
	}

	@Override
	public boolean exists() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getFilename() {
		return this.path;
	}


	@Override
	public InputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return new FileOutputStream(this.path);
	}

}
