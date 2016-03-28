package org.pac4j.core.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileSystemResource implements WritableResource {

	private String path;

	public FileSystemResource(String path) {
		this.path = path;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getFilename() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return new FileOutputStream(this.path);
	}

}
