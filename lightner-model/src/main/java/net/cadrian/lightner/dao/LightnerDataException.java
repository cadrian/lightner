package net.cadrian.lightner.dao;

import java.io.IOException;

public class LightnerDataException extends IOException {

	private static final long serialVersionUID = 1401622248736643734L;

	public LightnerDataException() {
		super();
	}

	public LightnerDataException(final String msg) {
		super(msg);
	}

	public LightnerDataException(final Throwable t) {
		super(t);
	}

	public LightnerDataException(final String msg, final Throwable t) {
		super(msg, t);
	}

}
