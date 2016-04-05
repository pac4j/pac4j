package org.pac4j.core.io;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.core.io.Resource;

/**
 * A wrapper that adapts a spring {@link org.springframework.core.io.WritableResource} to a pac4j {@link WritableResource}.
 * 
 * @author Keith Garry Boyce
 * @since 1.4.3
 */
public class SpringWritableResourceWrapper extends SpringResourceWrapper implements WritableResource {

	public SpringWritableResourceWrapper(Resource springResource) {
		super(springResource);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return ((org.springframework.core.io.WritableResource) springResource).getOutputStream();
	}

}
